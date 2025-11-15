package com.jeongseonghun.urlshortener.api.dto;

import com.jeongseonghun.urlshortener.domain.ShortUrl;

public record ShortenResponse(String shortUrl) {
    public static ShortenResponse from(ShortUrl shortUrl) {
        //TODO getShortUrl 할때 파라미터 제거 작업 필요
        return new ShortenResponse(shortUrl.getShortUrl("temp"));
    }
}
