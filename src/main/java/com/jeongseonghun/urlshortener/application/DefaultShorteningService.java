package com.jeongseonghun.urlshortener.application;

import com.jeongseonghun.urlshortener.api.dto.ShortUrlResponse;
import com.jeongseonghun.urlshortener.config.AppProperties;
import com.jeongseonghun.urlshortener.domain.*;
import com.jeongseonghun.urlshortener.infrastructure.ShortUrlCreationPolicyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultShorteningService implements ShorteningService {

    private final ShortUrlWriter shortUrlWriter;
    private final ShortUrlReader shortUrlReader;
    private final ShortKeyGenerator shortKeyGenerator;
    private final AppProperties appProperties;
    private final ShortUrlCreationPolicyManager shortUrlCreationPolicyManager;

    @Override
    @Transactional
    public ShortUrlResponse getOrCreateShortUrl(String rawUrl) {
        OriginalUrl originalUrl = OriginalUrl.of(rawUrl);
        shortUrlCreationPolicyManager.execute(originalUrl);
        Optional<ShortUrl> existingShortUrl = shortUrlReader.findShortUrl(originalUrl);
        if (existingShortUrl.isPresent()) {
            return ShortUrlResponse.from(existingShortUrl.get(), appProperties.getDomain());
        }
        ShortUrl newShortUrl = new ShortUrl(originalUrl, shortKeyGenerator.generateShortKey());
        shortUrlWriter.save(newShortUrl);
        return ShortUrlResponse.from(newShortUrl, appProperties.getDomain());
    }

}
