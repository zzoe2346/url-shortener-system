package com.jeongseonghun.urlshortener.domain;

import jakarta.servlet.http.HttpServletRequest;

public interface RedirectService {
    /**
     * 단축 URL 키에 매핑된 원본 URL을 조회.
     *
     * @return 매핑된 원본 URL
     */
    String getOriginalUrl(String shortKey, HttpServletRequest request);
}
