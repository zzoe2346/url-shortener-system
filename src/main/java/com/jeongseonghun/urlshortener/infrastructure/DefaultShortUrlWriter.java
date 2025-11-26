package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.ShortUrl;
import com.jeongseonghun.urlshortener.domain.ShortUrlWriter;
import com.jeongseonghun.urlshortener.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class DefaultShortUrlWriter implements ShortUrlWriter {

    private final ShortUrlRepository shortUrlRepository;

    public CompletableFuture<Void> save(ShortUrl shortUrl) {
        shortUrlRepository.save(shortUrl);
        return CompletableFuture.completedFuture(null);
    }

}
