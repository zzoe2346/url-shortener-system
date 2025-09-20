package com.jeongseonghun.urlshortener.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class URL {
    @Id
    private Long id;
    @Column(nullable = false, unique = true)
    private String originalUrl;
    @Column(nullable = false, unique = true)
    private String shortenUrl;

    public URL(String originalUrl, String shortenUrl) {
        this.originalUrl = originalUrl;
        this.shortenUrl = shortenUrl;
    }
}
