package com.jeongseonghun.urlshortener.repository;

import com.jeongseonghun.urlshortener.domain.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findByShortCode(String shortCode);
    Optional<ShortUrl> findByOriginalUrl(String originalUrl);
    Optional<ShortUrl> findTopByOrderByIdDesc();
}
