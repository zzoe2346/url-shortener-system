package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.ShortUrl;
import com.jeongseonghun.urlshortener.domain.ShortUrlWriter;
import com.jeongseonghun.urlshortener.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncShortUrlWriter implements ShortUrlWriter {

    private final ShortUrlRepository shortUrlRepository;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 100;

    @Async
    @Override
    public CompletableFuture<Void> save(ShortUrl shortUrl) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                shortUrlRepository.save(shortUrl);
                return CompletableFuture.completedFuture(null); // 성공 시 완료된 Future 반환
            } catch (Exception e) {
                log.warn("비동기 저장 실패. [시도 {}/{}] URL: {}", attempt, MAX_RETRIES, shortUrl.getOriginalUrl().getValue(), e);
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("비동기 저장 재시도 대기 중 인터럽트 발생. URL: {}", shortUrl.getOriginalUrl().getValue(), ie);
                        return CompletableFuture.failedFuture(ie);
                    }
                }
            }
        }
        log.error("비동기 저장 최종 실패. URL: {}", shortUrl.getOriginalUrl().getValue());
        return CompletableFuture.failedFuture(new RuntimeException("비동기 저장 최종 실패"));
    }
}
