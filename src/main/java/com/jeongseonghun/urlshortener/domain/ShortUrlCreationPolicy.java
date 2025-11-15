package com.jeongseonghun.urlshortener.domain;

public interface ShortUrlCreationPolicy {
    void validate(OriginalUrl originalUrl);
}
