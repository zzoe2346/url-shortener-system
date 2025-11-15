package com.jeongseonghun.urlshortener.domain;

import jakarta.servlet.http.HttpServletRequest;

public interface ClickLogWriter {

    void recordClick(HttpServletRequest request, ShortUrl shortUrl);
}
