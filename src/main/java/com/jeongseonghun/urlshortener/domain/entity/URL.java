package com.jeongseonghun.urlshortener.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class URL {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
