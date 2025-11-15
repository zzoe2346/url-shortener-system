package com.jeongseonghun.urlshortener.domain;

public interface ShortUrlWriter {
    /**
     * URL 단축 요청 시 응답 지연을 최소화하기 위해 비동기적으로 URL 매핑을 DB에 저장
     * @param originalUrl
     * @param shortCode
     */
    void saveToDbAsync(ShortUrl shortUrl);
}
