package com.jeongseonghun.urlshortener.domain;

import jakarta.persistence.*;
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
            column = @Column(name = "original_url", nullable = false, unique = true)
    )
    private OriginalUrl originalUrl;
    @Column(nullable = false)
    private String shortKey;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ShortUrl(OriginalUrl originalUrl, String shortKey) {
        this.validate(shortKey);
        this.originalUrl = originalUrl;
        this.shortKey = shortKey;
    }

    private void validate(String value) {
        if (!value.matches("^[a-zA-Z0-9]+$")) {
            throw new ValidationException("ShortKey는 영문 대소문자와 숫자로만 구성되어야 합니다.");
        }
    }

    public String getShortUrl(String domain) {
        return "https://" + domain + "/" + this.shortKey;
    }
}
