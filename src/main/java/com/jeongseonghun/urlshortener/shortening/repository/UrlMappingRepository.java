package com.jeongseonghun.urlshortener.shortening.repository;

import com.jeongseonghun.urlshortener.shortening.model.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    Optional<UrlMapping> findByShortCode(String shortCode);
    Optional<UrlMapping> findByOriginalUrl(String originalUrl);
    Optional<UrlMapping> findTopByOrderByIdDesc();
}
