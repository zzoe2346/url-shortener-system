package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.ValidationHandler;
import com.jeongseonghun.urlshortener.domain.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ShortCodeFormatHandler implements ValidationHandler {

    private ValidationHandler next;

    @Override
    public void setNext(ValidationHandler next) {
        this.next = next;
    }

    @Override
    public void validate(String originalUrl) throws ValidationException {
        // Shorten용 (사용 안 함)
    }

    @Override
    public void validate(String shortCode, HttpServletRequest request) throws ValidationException {
        if (!shortCode.matches("^[a-zA-Z0-9]+$")) {
            throw new ValidationException("ShortCode는 영문 대소문자와 숫자로만 구성되어야 합니다.");
        }
        if (next != null) {
            next.validate(shortCode, request);
        }
    }
}
