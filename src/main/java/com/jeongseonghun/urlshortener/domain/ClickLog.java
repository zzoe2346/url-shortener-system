package com.jeongseonghun.urlshortener.domain;

import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ClickLog {

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

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
        this.ipAddress = request.getHeader(X_FORWARDED_FOR);
        this.userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        this.referrer = request.getHeader(HttpHeaders.REFERER);
        this.acceptLanguage = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
    }
}
