package com.jeongseonghun.urlshortener.domain;

import java.util.concurrent.CompletableFuture;

public interface ShortUrlWriter {
    /**
     * ShortUrl 저장
     * @param shortUrl
     * @return
     */
    CompletableFuture<Void> save(ShortUrl shortUrl);
}
