package com.jeongseonghun.urlshortener.shortening.service;

public interface ShorteningService {
    String shortenUrl(String originalUrl);
    String getOriginalUrl(String shortUrl);
}
