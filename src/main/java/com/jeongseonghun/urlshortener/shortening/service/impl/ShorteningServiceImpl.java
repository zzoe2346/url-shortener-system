package com.jeongseonghun.urlshortener.shortening.service.impl;

import com.jeongseonghun.urlshortener.shortening.model.entity.Url;
import com.jeongseonghun.urlshortener.shortening.repository.UrlRepository;
import com.jeongseonghun.urlshortener.common.exception.UrlNotFoundException;
import com.jeongseonghun.urlshortener.shortening.service.IdSupplier;
import com.jeongseonghun.urlshortener.shortening.service.ShorteningService;
import com.jeongseonghun.urlshortener.common.util.Base62;
import org.springframework.stereotype.Service;

@Service
public class ShorteningServiceImpl implements ShorteningService {

    private final UrlRepository urlRepository;
    private final IdSupplier idSupplier;

    public ShorteningServiceImpl(UrlRepository urlRepository, IdSupplier idSupplier) {
        this.urlRepository = urlRepository;
        this.idSupplier = idSupplier;
    }

    public String shortenUrl(String originalUrl) {
        return urlRepository.findShortenUrlByOriginalUrl(originalUrl)
                .orElseGet(() -> {
                            String shortenUrl = Base62.Encoder.encode(idSupplier.getId());
                            urlRepository.save(new Url(originalUrl, shortenUrl));
                            return shortenUrl;
                        }
                );
    }

    public String getOriginalUrl(String shortUrl) {
        return urlRepository.findOriginalUrlByShortenUrl(shortUrl)
                .orElseThrow(() -> new UrlNotFoundException("해당 단축 URL을 찾을 수 없습니다: " + shortUrl));
    }
}
