package com.jeongseonghun.urlshortener.application;

import com.jeongseonghun.urlshortener.domain.*;
import com.jeongseonghun.urlshortener.support.Message;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultRedirectService implements RedirectService {

    private final ShortUrlReader shortUrlReader;
    private final ClickLogWriter clickLogWriter;

    @Override
    @Transactional
    public String getOriginalUrl(String shortKey, HttpServletRequest request) {
        ShortUrl shortUrl = shortUrlReader.findShortUrlByShortKey(shortKey)
                .orElseThrow(() -> new UrlNotFoundException(Message.SHORTKEY_NOT_FOUND));
        clickLogWriter.save(new ClickLog(shortUrl, request));
        return shortUrl.getOriginalUrl().getValue();
    }

}
