package com.jeongseonghun.urlshortener.domain.repository;

import com.jeongseonghun.urlshortener.domain.entity.URL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<URL, Long> {

    @Query("SELECT u.shortenUrl " +
            "FROM URL u " +
            "WHERE u.originalUrl = :originalUrl")
    Optional<String> findShortenUrlByOriginalUrl(String originalUrl);

    @Query("SELECT u.originalUrl " +
            "FROM URL u " +
            "WHERE u.shortenUrl = :shortenUrl")
    Optional<String> findOriginalUrlByShortenUrl(String shortenUrl);
}
