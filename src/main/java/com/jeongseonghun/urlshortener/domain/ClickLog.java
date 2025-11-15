package com.jeongseonghun.urlshortener.domain;

import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ClickLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "short_url_id")
    private ShortUrl shortUrl;
    private String ipAddress;
    private String userAgent;
    private String referrer;
    private String acceptLanguage;
    @CreationTimestamp
    private LocalDateTime clickedAt;

    public ClickLog(ShortUrl shortUrl, HttpServletRequest request) {
        this.shortUrl = shortUrl;
        this.ipAddress = request.getHeader("X-Forwarded-For");
        this.userAgent = request.getHeader("User-Agent");
        this.referrer = request.getHeader("Referer");
        this.acceptLanguage = request.getHeader("Accept-Language");
    }
}
