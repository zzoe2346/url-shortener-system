package com.jeongseonghun.urlshortener.domain;

import com.jeongseonghun.urlshortener.support.Message;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.util.InvalidUrlException;

import java.net.URI;
import java.net.URISyntaxException;

@Embeddable
@NoArgsConstructor
@Getter
public class OriginalUrl {

    private String value;

    //TODO 도메인 얻는 로직이 좀 비효율적으로 보임. 이 클래스 초기화 시점에서 도메인 세팅하는 걸로 변경 고려.
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

    public String getDomain() {
        try {
            return new URI(this.value).getHost();
        } catch (URISyntaxException e) {
            throw new InvalidUrlException(Message.INVALID_URL_FORMAT);
        }
    }

}
