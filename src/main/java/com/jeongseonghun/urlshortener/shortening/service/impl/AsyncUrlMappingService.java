package com.jeongseonghun.urlshortener.shortening.service.impl;

import com.jeongseonghun.urlshortener.shortening.model.entity.UrlMapping;
import com.jeongseonghun.urlshortener.shortening.repository.UrlMappingRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncUrlMappingService {

    private final UrlMappingRepository urlMappingRepository;

    public AsyncUrlMappingService(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    /**
     * URL 단축 요청 시 응답 지연을 최소화하기 위해 비동기적으로 URL 매핑을 DB에 저장
     */
    @Async
    public void saveToDbAsync(String originalUrl, String shortCode) {
        urlMappingRepository.save(new UrlMapping(originalUrl, shortCode));
    }
}
