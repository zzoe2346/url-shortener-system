package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.ClickLog;
import com.jeongseonghun.urlshortener.domain.ClickLogService;
import com.jeongseonghun.urlshortener.domain.ShortUrl;
import com.jeongseonghun.urlshortener.repository.ClickLogRepository;
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
    public void recordClick(HttpServletRequest request, ShortUrl shortUrl) {
        ClickLog clickLog = new ClickLog();
        clickLog.setShortUrl(shortUrl);
        clickLog.setIpAddress(request.getHeader("X-Forwarded-For"));
        clickLog.setUserAgent(request.getHeader("User-Agent"));
        clickLog.setReferrer(request.getHeader("Referer"));
        clickLog.setAcceptLanguage(request.getHeader("Accept-Language"));
        clickLog.setClickedAt(LocalDateTime.now());
        clickLogRepository.save(clickLog);
    }
}
