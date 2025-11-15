package com.jeongseonghun.urlshortener.application;

import com.jeongseonghun.urlshortener.domain.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultRedirectService implements RedirectService {

    private final ShortUrlReader shortUrlReader;
    private final ClickLogWriter clickLogWriter;

    @Override
    public String getOriginalUrl(String shortKey, HttpServletRequest request) {
        ShortUrl shortUrl = shortUrlReader.findShortUrlByShortKey(shortKey)
                .orElseThrow(() -> new UrlNotFoundException("해당 ShortKey 를 찾을 수 없습니다: " + shortKey));
        clickLogWriter.save(new ClickLog(shortUrl, request));
        return shortUrl.getOriginalUrl().getValue();
    }
}
