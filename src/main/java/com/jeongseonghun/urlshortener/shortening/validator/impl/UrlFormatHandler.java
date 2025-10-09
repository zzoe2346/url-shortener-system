package com.jeongseonghun.urlshortener.shortening.validator.impl;

import com.jeongseonghun.urlshortener.common.exception.ValidationException;
import com.jeongseonghun.urlshortener.shortening.validator.ValidationHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class UrlFormatHandler implements ValidationHandler {

    private ValidationHandler next;

    @Override
    public void setNext(ValidationHandler next) {
        this.next = next;
    }

    @Override
    public void validate(String originalUrl) throws ValidationException {
        if (originalUrl.isEmpty()) {
            throw new ValidationException("URL이 없습니다.");
        }
        if (!originalUrl.matches("^(https?://).+")) {
            throw new ValidationException("URL은 http:// 또는 https:// 로 시작해야 합니다.");
        }
        if (next != null) {
            next.validate(originalUrl);
        }
    }

    @Override
    public void validate(String shortCode, HttpServletRequest request) throws ValidationException {
        // Redirection용 (사용 안 함)
    }
}
