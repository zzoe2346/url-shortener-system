package com.jeongseonghun.urlshortener.application;

import com.jeongseonghun.urlshortener.config.AppProperties;
import com.jeongseonghun.urlshortener.domain.*;
import com.jeongseonghun.urlshortener.domain.UrlNotFoundException;
import com.jeongseonghun.urlshortener.infrastructure.AsyncShortUrlWriter;
import com.jeongseonghun.urlshortener.support.Base62;
import com.jeongseonghun.urlshortener.repository.ShortUrlRepository;
import com.jeongseonghun.urlshortener.domain.ValidationHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class DefaultShorteningService implements ShorteningService {

    private final ShortUrlRepository shortUrlRepository;
    private final IdSupplier idSupplier;
    private final ClickLogWriter clickLogWriter;
    private final ValidationHandler shortenChain;
    private final ValidationHandler redirectChain;
    private final AppProperties appProperties;
    private final AsyncShortUrlWriter asyncUrlMappingService;
    private final RedissonClient redissonClient;

    public DefaultShorteningService(ShortUrlRepository shortUrlRepository,
                                    IdSupplier idSupplier,
                                    ClickLogWriter clickLogWriter,
                                    @Qualifier("shortenValidationChain") ValidationHandler shortenChain,
                                    @Qualifier("redirectValidationChain") ValidationHandler redirectChain,
                                    AppProperties appProperties,
                                    AsyncShortUrlWriter asyncUrlMappingService,
                                    RedissonClient redissonClient) {
        this.shortUrlRepository = shortUrlRepository;
        this.idSupplier = idSupplier;
        this.clickLogWriter = clickLogWriter;
        this.shortenChain = shortenChain;
        this.redirectChain = redirectChain;
        this.appProperties = appProperties;
        this.asyncUrlMappingService = asyncUrlMappingService;
        this.redissonClient = redissonClient;
    }

    public String getOrCreateShortUrl(String originalUrl) {
        shortenChain.validate(originalUrl);
        Optional<ShortUrl> urlMapping = shortUrlRepository.findByOriginalUrl(originalUrl);
        // 원본 URL과 매핑된 단축 URL이 있는 경우: 매핑된 단축 URL을 리턴
        if (urlMapping.isPresent()) {
            return urlMapping.get().getShortUrl(appProperties.getDomain());
        }
        // 원본 URL과 매핑된 단축 URL이 없는 경우: 단축 URL 새로 생성하고 매핑 시킴
        // 1. originalUrl 을 이름으로 하는 락을 획득
        RLock lock = redissonClient.getLock(originalUrl);

        try {
            boolean isLocked = lock.tryLock(3, 1, TimeUnit.SECONDS);
            // 2-1. 락 획득에 실패하면 예외를 던짐
            if (!isLocked) {
                throw new RuntimeException("잠시 후 다시 시도해주세요.");
            }
            // 2-2. 락 획득에 성공한 경우 ID 채번기로부터 ID를 얻어서 Base62 인코딩을 하여 새로운 ShortCode를 생성
            String newShortCode = Base62.Encoder.encode(idSupplier.getId());
            // 3. DB에 원본 URL - 단축 URL(shortCode) 매핑 되도록 저장
            // => 비동기 처리
            ShortUrl newShortUrl = new ShortUrl(originalUrl, newShortCode);
            asyncUrlMappingService.saveToDbAsync(originalUrl, newShortCode);
            // 4. 단축 URL 리턴
            return newShortUrl.getShortUrl(appProperties.getDomain());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 락은 꼭 해제할 것!
            lock.unlock();
        }
    }

    public String getOriginalUrl(String shortCode, HttpServletRequest request) {
        redirectChain.validate(shortCode, request);
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("해당 단축 코드를 찾을 수 없습니다: " + shortCode));
        clickLogWriter.recordClick(request, shortUrl);
        return shortUrl.getOriginalUrl();
    }
}
