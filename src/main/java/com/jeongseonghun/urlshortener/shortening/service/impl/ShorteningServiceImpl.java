package com.jeongseonghun.urlshortener.shortening.service.impl;

import com.jeongseonghun.urlshortener.clicklog.service.ClickLogService;
import com.jeongseonghun.urlshortener.shortening.model.entity.UrlMapping;
import com.jeongseonghun.urlshortener.shortening.repository.UrlMappingRepository;
import com.jeongseonghun.urlshortener.common.exception.UrlNotFoundException;
import com.jeongseonghun.urlshortener.shortening.service.IdSupplier;
import com.jeongseonghun.urlshortener.shortening.service.ShorteningService;
import com.jeongseonghun.urlshortener.common.util.Base62;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class ShorteningServiceImpl implements ShorteningService {

    private final UrlMappingRepository urlMappingRepository;
    private final IdSupplier idSupplier;
    private final ClickLogService clickLogService;

    public ShorteningServiceImpl(UrlMappingRepository urlMappingRepository, IdSupplier idSupplier, ClickLogService clickLogService) {
        this.urlMappingRepository = urlMappingRepository;
        this.idSupplier = idSupplier;
        this.clickLogService = clickLogService;
    }

    public String shortenUrl(String originalUrl) {
        return urlMappingRepository.findShortenUrlByOriginalUrl(originalUrl)
                .orElseGet(() -> {
                            String shortenUrl = Base62.Encoder.encode(idSupplier.getId());
                            urlMappingRepository.save(new UrlMapping(originalUrl, shortenUrl));
                            return shortenUrl;
                        }
                );
    }

    public String getOriginalUrl(String shortUrl, HttpServletRequest request) {
        UrlMapping urlMapping = urlMappingRepository.findUrlMappingByShortenUrl(shortUrl)
                .orElseThrow(() -> new UrlNotFoundException("해당 단축 URL을 찾을 수 없습니다: " + shortUrl));

        clickLogService.recordClick(request, urlMapping);

        return urlMapping.getOriginalUrl();
    }
}
