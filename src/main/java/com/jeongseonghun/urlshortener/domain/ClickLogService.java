package com.jeongseonghun.urlshortener.domain;

import jakarta.servlet.http.HttpServletRequest;

public interface ClickLogService {

    void recordClick(HttpServletRequest request, UrlMapping urlMapping);
}
