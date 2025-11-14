package com.jeongseonghun.urlshortener.domain;

import jakarta.servlet.http.HttpServletRequest;

/**
 * URL 단축 기능의 핵심 비즈니스 로직을 정의하는 인터페이스.
 */
public interface ShorteningService {
    /**
     * 원본 URL을 기반으로 단축 URL 키를 생성하거나 기존에 생성된 키를 조회.
     * 이 메서드는 멱등성(Idempotent)을 보장한다. 즉, 동일한 원본 URL에 대해서는 항상 동일한 단축 URL을 반환.
     *
     * @param originalUrl 원본 URL
     * @return 생성되었거나 조회된 단축 URL
     */
    String getOrCreateShortUrl(String originalUrl);

    /**
     * 단축 URL 키에 매핑된 원본 URL을 조회.
     *
     * @param shortCode 단축 코드
     * @return 매핑된 원본 URL
     */
    String getOriginalUrl(String shortCode, HttpServletRequest request);
}
