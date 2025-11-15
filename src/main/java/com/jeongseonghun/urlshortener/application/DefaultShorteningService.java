package com.jeongseonghun.urlshortener.application;

import com.jeongseonghun.urlshortener.api.dto.ShortUrlResponse;
import com.jeongseonghun.urlshortener.config.AppProperties;
import com.jeongseonghun.urlshortener.domain.*;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DefaultShorteningService implements ShorteningService {

    private final ShortUrlWriter shortUrlWriter;
    private final RedissonClient redissonClient;
    private final ShortUrlReader shortUrlReader;
    private final ShortKeyGenerator shortKeyGenerator;
    private final AppProperties appProperties;

    public ShortUrlResponse getOrCreateShortUrl(String rawUrl) {
        OriginalUrl originalUrl = OriginalUrl.of(rawUrl);
        Optional<ShortUrl> existingShortUrl = shortUrlReader.findShortUrl(originalUrl);
        if (existingShortUrl.isPresent()) {
            return ShortUrlResponse.from(existingShortUrl.get(), appProperties.getDomain());
        }
        RLock lock = redissonClient.getLock(originalUrl.getValue());
        try {
            boolean isLocked = lock.tryLock(3, 1, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException("잠시 후 다시 시도해주세요.");
            }
            ShortUrl newShortUrl = new ShortUrl(originalUrl, shortKeyGenerator.generateShortKey());
            shortUrlWriter.save(newShortUrl);
            return ShortUrlResponse.from(newShortUrl, appProperties.getDomain());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

}
