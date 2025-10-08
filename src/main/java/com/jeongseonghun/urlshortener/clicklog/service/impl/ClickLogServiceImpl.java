package com.jeongseonghun.urlshortener.clicklog.service.impl;

import com.jeongseonghun.urlshortener.clicklog.model.ClickLog;
import com.jeongseonghun.urlshortener.clicklog.repository.ClickLogRepository;
import com.jeongseonghun.urlshortener.clicklog.service.ClickLogService;
import com.jeongseonghun.urlshortener.shortening.model.entity.UrlMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClickLogServiceImpl implements ClickLogService {
    private final ClickLogRepository clickLogRepository;

    public ClickLogServiceImpl(ClickLogRepository clickLogRepository) {
        this.clickLogRepository = clickLogRepository;
    }

    @Override
    public void recordClick(HttpServletRequest request, UrlMapping urlMapping) {
        ClickLog clickLog = new ClickLog();
        clickLog.setUrlMapping(urlMapping);
        clickLog.setIpAddress(request.getHeader("X-Forwarded-For"));
        clickLog.setUserAgent(request.getHeader("User-Agent"));
        clickLog.setReferrer(request.getHeader("Referer"));
        clickLog.setAcceptLanguage(request.getHeader("Accept-Language"));
        clickLog.setClickedAt(LocalDateTime.now());
        clickLogRepository.save(clickLog);
    }
}
