package com.jeongseonghun.urlshortener.application;

import com.jeongseonghun.urlshortener.api.dto.ShortUrlResponse;
import com.jeongseonghun.urlshortener.config.AppProperties;
import com.jeongseonghun.urlshortener.domain.*;
import com.jeongseonghun.urlshortener.infrastructure.ShortUrlCreationPolicyManager;
import com.jeongseonghun.urlshortener.support.Message;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ShortUrlCreationPolicyManager shortUrlCreationPolicyManager;

    private static final long WAIT_TIME = 2;

    @Override
    @Transactional
    public ShortUrlResponse getOrCreateShortUrl(String rawUrl) {
        OriginalUrl originalUrl = OriginalUrl.of(rawUrl);
        shortUrlCreationPolicyManager.execute(originalUrl);
        Optional<ShortUrl> existingShortUrl = shortUrlReader.findShortUrl(originalUrl);
        if (existingShortUrl.isPresent()) {
            return ShortUrlResponse.from(existingShortUrl.get(), appProperties.getDomain());
        }
        RLock lock = redissonClient.getLock(originalUrl.getValue());
        try {
            boolean isLocked = lock.tryLock(WAIT_TIME, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException(Message.RETRY_LATER);
            }
            ShortUrl newShortUrl = new ShortUrl(originalUrl, shortKeyGenerator.generateShortKey());
            shortUrlWriter.save(newShortUrl);
            return ShortUrlResponse.from(newShortUrl, appProperties.getDomain());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
