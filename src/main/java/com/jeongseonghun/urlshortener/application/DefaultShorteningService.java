package com.jeongseonghun.urlshortener.application;

import com.jeongseonghun.urlshortener.api.dto.ShortenResponse;
import com.jeongseonghun.urlshortener.domain.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DefaultShorteningService implements ShorteningService {

    private final ClickLogWriter clickLogWriter;
    private final ShortUrlWriter shortUrlWriter;
    private final RedissonClient redissonClient;
    private final ShortUrlReader shortUrlReader;
    private final ShortKeyGenerator shortKeyGenerator;

    public ShortenResponse getOrCreateShortUrl(String rawUrl) {
        OriginalUrl originalUrl = OriginalUrl.of(rawUrl);
        Optional<ShortUrl> existingShortUrl = shortUrlReader.findShortUrl(originalUrl);
        if (existingShortUrl.isPresent()) {
            return ShortenResponse.from(existingShortUrl.get());
        }
        // 1. originalUrl 을 이름으로 하는 락을 획득
        RLock lock = redissonClient.getLock(originalUrl.getValue());
        try {
            boolean isLocked = lock.tryLock(3, 1, TimeUnit.SECONDS);
            // 2-1. 락 획득에 실패하면 예외를 던짐
            if (!isLocked) {
                throw new RuntimeException("잠시 후 다시 시도해주세요.");
            }
            // 2-2. 락 획득에 성공한 경우 ID 채번기로부터 ID를 얻어서 Base62 인코딩을 하여 새로운 ShortCode를 생성
            // 3. DB에 원본 URL - 단축 URL(shortCode) 매핑 되도록 저장
            // => 비동기 처리
            ShortUrl newShortUrl = new ShortUrl(originalUrl, shortKeyGenerator.generateShortKey());
            shortUrlWriter.saveToDbAsync(newShortUrl);
            // 4. 단축 URL 리턴
            return ShortenResponse.from(newShortUrl);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 락은 꼭 해제할 것!
            lock.unlock();
        }
    }

    public String getOriginalUrl(String shortKey, HttpServletRequest request) {
        ShortUrl shortUrl = shortUrlReader.findShortUrlByShortKey(shortKey)
                .orElseThrow(() -> new UrlNotFoundException("해당 ShortKey 를 찾을 수 없습니다: " + shortKey));
        clickLogWriter.save(new ClickLog(shortUrl, request));
        return shortUrl.getOriginalUrl().getValue();
    }
}
