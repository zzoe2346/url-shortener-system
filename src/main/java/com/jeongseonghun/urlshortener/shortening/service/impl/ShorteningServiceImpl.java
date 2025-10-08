package com.jeongseonghun.urlshortener.shortening.service.impl;

import com.jeongseonghun.urlshortener.clicklog.service.ClickLogService;
import com.jeongseonghun.urlshortener.common.config.AppProperties;
import com.jeongseonghun.urlshortener.common.exception.UrlNotFoundException;
import com.jeongseonghun.urlshortener.common.util.Base62;
import com.jeongseonghun.urlshortener.shortening.model.entity.UrlMapping;
import com.jeongseonghun.urlshortener.shortening.repository.UrlMappingRepository;
import com.jeongseonghun.urlshortener.shortening.service.IdSupplier;
import com.jeongseonghun.urlshortener.shortening.service.ShorteningService;
import com.jeongseonghun.urlshortener.shortening.validator.ValidationHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ShorteningServiceImpl implements ShorteningService {

    private final UrlMappingRepository urlMappingRepository;
    private final IdSupplier idSupplier;
    private final ClickLogService clickLogService;
    private final ValidationHandler shortenChain;
    private final ValidationHandler redirectChain;
    private final AppProperties appProperties;

    public ShorteningServiceImpl(UrlMappingRepository urlMappingRepository,
                                 IdSupplier idSupplier,
                                 ClickLogService clickLogService,
                                 @Qualifier("shortenValidationChain") ValidationHandler shortenChain,
                                 @Qualifier("redirectValidationChain") ValidationHandler redirectChain, AppProperties appProperties) {
        this.urlMappingRepository = urlMappingRepository;
        this.idSupplier = idSupplier;
        this.clickLogService = clickLogService;
        this.shortenChain = shortenChain;
        this.redirectChain = redirectChain;
        this.appProperties = appProperties;
    }

    public String getOrCreateShortUrl(String originalUrl) {
        shortenChain.validate(originalUrl);

        UrlMapping urlMapping = urlMappingRepository.findByOriginalUrl(originalUrl)
                .orElseGet(() -> {
                            String shortCode = Base62.Encoder.encode(idSupplier.getId());
                            return urlMappingRepository.save(new UrlMapping(originalUrl, shortCode));
                        }
                );
        return urlMapping.getShortUrl(appProperties.getDomain());
    }

    public String getOriginalUrl(String shortCode, HttpServletRequest request) {
        redirectChain.validate(shortCode, request);

        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("해당 단축 코드를 찾을 수 없습니다: " + shortCode));

        clickLogService.recordClick(request, urlMapping);

        return urlMapping.getOriginalUrl();
    }
}
