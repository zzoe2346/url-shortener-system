package com.jeongseonghun.urlshortener.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.net.URI;

@Embeddable
@Getter
public class OriginalUrl {

    private String value;

    private OriginalUrl(String value) {
        this.validate(value);
        this.value = value.trim();
    }

    public OriginalUrl() {

    }

    public static OriginalUrl of(String value) {
        return new OriginalUrl(value);
    }

    private void validate(String url) {
        try {
            new URI(url);
        } catch (Exception e) {
            throw new IllegalArgumentException(url);
        }
    }

}
