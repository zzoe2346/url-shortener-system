package com.jeongseonghun.urlshortener.domain;

import com.jeongseonghun.urlshortener.support.Message;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@Embeddable
@NoArgsConstructor
@Getter
public class OriginalUrl {

    private URL url;

    private OriginalUrl(String value) {
        try {
            this.url = URI.create(value).toURL();
        } catch (MalformedURLException e) {
            throw new ValidationException(Message.INVALID_URL_FORMAT);
        }
    }

    public static OriginalUrl of(String value) {
        return new OriginalUrl(value);
    }

    public String getDomain() {
        return this.url.getHost();
    }

}
