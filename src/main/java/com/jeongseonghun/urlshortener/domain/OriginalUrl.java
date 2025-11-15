package com.jeongseonghun.urlshortener.domain;

import com.jeongseonghun.urlshortener.support.Message;
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
            throw new ValidationException(Message.INVALID_URL_FORMAT);
        }
    }

}
