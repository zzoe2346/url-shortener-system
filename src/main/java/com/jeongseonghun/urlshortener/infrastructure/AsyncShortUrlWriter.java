package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.ShortUrl;
import com.jeongseonghun.urlshortener.domain.ShortUrlWriter;
import com.jeongseonghun.urlshortener.repository.ShortUrlRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncShortUrlWriter implements ShortUrlWriter {

    private final ShortUrlRepository shortUrlRepository;

    public AsyncShortUrlWriter(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    @Async
    public void save(ShortUrl shortUrl) {
        shortUrlRepository.save(shortUrl);
    }

}
