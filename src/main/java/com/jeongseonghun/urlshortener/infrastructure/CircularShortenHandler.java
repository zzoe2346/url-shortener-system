package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.config.AppProperties;
import com.jeongseonghun.urlshortener.domain.ValidationException;
import com.jeongseonghun.urlshortener.domain.ValidationHandler;
import com.jeongseonghun.urlshortener.support.Message;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class CircularShortenHandler implements ValidationHandler {

    private final AppProperties appProperties;
    private ValidationHandler next;

    public CircularShortenHandler(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public void setNext(ValidationHandler next) {
        this.next = next;
    }

    @Override
    public void validate(String originalUrl) throws ValidationException {
        if (appProperties.getDomain().equals(extractDomain(originalUrl))) {
            throw new ValidationException(Message.DUPLICATE_URL);
        }
        if (next != null) {
            next.validate(originalUrl);
        }
    }

    /**
     * URL 문자열에서 도메인 부분을 추출하는 메서드.
     *
     * @param originalUrl 도메인 추출할 URL 문자열
     * @return 도메인 문자열
     */
    private String extractDomain(String originalUrl) {
        return URI.create(originalUrl).getHost();
    }

    @Override
    public void validate(String shortCode, HttpServletRequest request) throws ValidationException {
        // Redirection용 (사용 안 함)
    }
}
