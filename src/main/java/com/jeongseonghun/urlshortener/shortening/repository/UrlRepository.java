package com.jeongseonghun.urlshortener.shortening.repository;

import com.jeongseonghun.urlshortener.shortening.model.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query("SELECT u.shortenUrl " +
            "FROM Url u " +
            "WHERE u.originalUrl = :originalUrl")
    Optional<String> findShortenUrlByOriginalUrl(String originalUrl);

    @Query("SELECT u.originalUrl " +
            "FROM Url u " +
            "WHERE u.shortenUrl = :shortenUrl")
    Optional<String> findOriginalUrlByShortenUrl(String shortenUrl);
}
