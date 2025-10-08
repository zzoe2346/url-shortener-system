package com.jeongseonghun.urlshortener.shortening.repository;

import com.jeongseonghun.urlshortener.shortening.model.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    @Query("SELECT u.shortenUrl " +
            "FROM UrlMapping u " +
            "WHERE u.originalUrl = :originalUrl")
    Optional<String> findShortenUrlByOriginalUrl(String originalUrl);

    @Query("SELECT u.originalUrl " +
            "FROM UrlMapping u " +
            "WHERE u.shortenUrl = :shortenUrl")
    Optional<String> findOriginalUrlByShortenUrl(String shortenUrl);

    Optional<UrlMapping> findUrlMappingByShortenUrl(String ShortenUrl);
    Optional<UrlMapping> findByOriginalUrl(String originalUrl);
}
