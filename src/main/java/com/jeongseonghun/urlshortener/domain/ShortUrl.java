package com.jeongseonghun.urlshortener.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class ShortUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "origin_url", nullable = false, unique = true)
    )
    private OriginalUrl originalUrl;
    @Column(nullable = false)
    private String shortCode;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public ShortUrl(OriginalUrl originalUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
    }

    public String getShortUrl(String domain) {
        return domain + "/" + this.shortCode;
    }
}
