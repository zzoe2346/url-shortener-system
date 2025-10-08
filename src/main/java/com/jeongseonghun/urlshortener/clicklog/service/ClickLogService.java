package com.jeongseonghun.urlshortener.clicklog.service;

import com.jeongseonghun.urlshortener.shortening.model.entity.UrlMapping;
import jakarta.servlet.http.HttpServletRequest;

public interface ClickLogService {

    void recordClick(HttpServletRequest request, UrlMapping urlMapping);
}
