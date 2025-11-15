package com.jeongseonghun.urlshortener.domain;

import java.util.Optional;

public interface ShortUrlReader {
    Optional<ShortUrl> findShortUrl(OriginalUrl originalUrl);
    Optional<ShortUrl> findShortUrlByShortKey(String shortCode);
}
