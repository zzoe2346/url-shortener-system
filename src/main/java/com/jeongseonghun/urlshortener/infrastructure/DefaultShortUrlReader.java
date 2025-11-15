package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.OriginalUrl;
import com.jeongseonghun.urlshortener.domain.ShortUrl;
import com.jeongseonghun.urlshortener.domain.ShortUrlReader;
import com.jeongseonghun.urlshortener.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DefaultShortUrlReader implements ShortUrlReader {

    private final ShortUrlRepository shortUrlRepository;

    @Override
    public Optional<ShortUrl> findShortUrl(OriginalUrl originalUrl) {
        return shortUrlRepository.findByOriginalUrl(originalUrl.getValue());
    }

    @Override
    public Optional<ShortUrl> findShortUrlByShortKey(String shortCode) {
        return shortUrlRepository.findByShortCode(shortCode);
    }

}
