package com.jeongseonghun.urlshortener.service;

import com.jeongseonghun.urlshortener.domain.entity.URL;
import com.jeongseonghun.urlshortener.domain.repository.UrlRepository;
import com.jeongseonghun.urlshortener.exception.UrlNotFoundException;
import com.jeongseonghun.urlshortener.idsupplier.IdSupplier;
import com.jeongseonghun.urlshortener.util.Base62;
import org.springframework.stereotype.Service;

@Service
public class ShortenService {

    private final UrlRepository urlRepository;
    private final IdSupplier idSupplier;

    public ShortenService(UrlRepository urlRepository, IdSupplier idSupplier) {
        this.urlRepository = urlRepository;
        this.idSupplier = idSupplier;
    }

    public String shortenUrl(String originalUrl) {
        return urlRepository.findShortenUrlByOriginalUrl(originalUrl)
                .orElseGet(() -> {
                            String shortenUrl = Base62.Encoder.encode(idSupplier.getId());
                            urlRepository.save(new URL(originalUrl, shortenUrl));
                            return shortenUrl;
                        }
                );
    }

    public String getOriginalUrl(String shortUrl) {
        return urlRepository.findOriginalUrlByShortenUrl(shortUrl)
                .orElseThrow(() -> new UrlNotFoundException("해당 단축 URL을 찾을 수 없습니다: " + shortUrl));
    }
}
