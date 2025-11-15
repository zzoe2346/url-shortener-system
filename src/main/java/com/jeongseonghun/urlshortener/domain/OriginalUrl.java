package com.jeongseonghun.urlshortener.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Getter
public class OriginalUrl {

    private String value;

    private OriginalUrl(String value) {
        this.validate(value);
        this.value = value.trim();
    }

    public static OriginalUrl of(String value) {
        return new OriginalUrl(value);
    }

    private void validate(String value) {
        if (!value.matches("^(https?://).+")) {
            throw new ValidationException("URL은 http:// 또는 https:// 로 시작해야 합니다.");
        }
    }

}
