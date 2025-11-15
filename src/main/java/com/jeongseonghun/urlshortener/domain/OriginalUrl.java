package com.jeongseonghun.urlshortener.domain;

import java.net.URI;

public class OriginalUrl {

    private String value;

    private OriginalUrl(String value) {
        this.validate(value);
        this.value = value.trim();
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

    public String value() {
        return value;
    }

}
