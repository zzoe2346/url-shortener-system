package com.jeongseonghun.urlshortener.api.dto;

import com.jeongseonghun.urlshortener.domain.ShortUrl;

public record ShortUrlResponse(String shortUrl) {
    public static ShortUrlResponse from(ShortUrl shortUrl, String domain) {
        return new ShortUrlResponse(shortUrl.getShortUrl(domain));
    }
}
