package com.jeongseonghun.urlshortener.clicklog.model;

import com.jeongseonghun.urlshortener.shortening.model.entity.Url;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ClickLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "url_id")
    private Url url;
    private String ipAddress;
    private String userAgent;
    private String referrer;
    private String acceptLanguage;
    private LocalDateTime clickedAt;
}
