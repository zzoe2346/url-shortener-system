package com.jeongseonghun.urlshortener.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ClickLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "url_mapping_id")
    private ShortUrl shortUrl;
    private String ipAddress;
    private String userAgent;
    private String referrer;
    private String acceptLanguage;
    private LocalDateTime clickedAt;
}
