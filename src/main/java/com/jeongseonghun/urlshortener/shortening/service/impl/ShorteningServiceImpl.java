package com.jeongseonghun.urlshortener.shortening.service.impl;

import com.jeongseonghun.urlshortener.clicklog.service.ClickLogService;
import com.jeongseonghun.urlshortener.common.config.AppProperties;
import com.jeongseonghun.urlshortener.common.exception.UrlNotFoundException;
import com.jeongseonghun.urlshortener.common.util.Base62;
import com.jeongseonghun.urlshortener.shortening.model.entity.UrlMapping;
import com.jeongseonghun.urlshortener.shortening.repository.UrlMappingRepository;
import com.jeongseonghun.urlshortener.shortening.service.IdSupplier;
import com.jeongseonghun.urlshortener.shortening.service.ShorteningService;
import com.jeongseonghun.urlshortener.shortening.validator.ValidationHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ShorteningServiceImpl implements ShorteningService {

    private final UrlMappingRepository urlMappingRepository;
    private final IdSupplier idSupplier;
    private final ClickLogService clickLogService;
    private final ValidationHandler shortenChain;
    private final ValidationHandler redirectChain;
    private final AppProperties appProperties;
    private final AsyncUrlMappingService asyncUrlMappingService;
    private final RedissonClient redissonClient;


    public ShorteningServiceImpl(UrlMappingRepository urlMappingRepository,
                                 IdSupplier idSupplier,
                                 ClickLogService clickLogService,
                                 @Qualifier("shortenValidationChain") ValidationHandler shortenChain,
                                 @Qualifier("redirectValidationChain") ValidationHandler redirectChain,
                                 AppProperties appProperties,
                                 AsyncUrlMappingService asyncUrlMappingService,
                                 RedissonClient redissonClient) {
        this.urlMappingRepository = urlMappingRepository;
        this.idSupplier = idSupplier;
        this.clickLogService = clickLogService;
        this.shortenChain = shortenChain;
        this.redirectChain = redirectChain;
        this.appProperties = appProperties;
        this.asyncUrlMappingService = asyncUrlMappingService;
        this.redissonClient = redissonClient;
    }

    public String getOrCreateShortUrl(String originalUrl) {
        shortenChain.validate(originalUrl);

        Optional<UrlMapping> urlMapping = urlMappingRepository.findByOriginalUrl(originalUrl);
        if (urlMapping.isPresent()) {
            return urlMapping.get().getShortUrl(appProperties.getDomain());
        }

        RLock lock = redissonClient.getLock(originalUrl);
        try {
            boolean isLocked = lock.tryLock(3, 1, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException("잠시 후 다시 시도해주세요.");
            }

            String newShortCode = Base62.Encoder.encode(idSupplier.getId());
            UrlMapping newUrlMapping = new UrlMapping(originalUrl, newShortCode);

            asyncUrlMappingService.saveToDbAsync(originalUrl, newShortCode);

            return newUrlMapping.getShortUrl(appProperties.getDomain());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public String getOriginalUrl(String shortCode, HttpServletRequest request) {
        redirectChain.validate(shortCode, request);

        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("해당 단축 코드를 찾을 수 없습니다: " + shortCode));

        clickLogService.recordClick(request, urlMapping);

        return urlMapping.getOriginalUrl();
    }
}
