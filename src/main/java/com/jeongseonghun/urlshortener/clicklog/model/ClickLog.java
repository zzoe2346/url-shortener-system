package com.jeongseonghun.urlshortener.clicklog.model;

import com.jeongseonghun.urlshortener.shortening.model.entity.UrlMapping;
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
    private UrlMapping urlMapping;
    private String ipAddress;
    private String userAgent;
    private String referrer;
    private String acceptLanguage;
    private LocalDateTime clickedAt;
}
