package com.jeongseonghun.urlshortener.shortening.validator.impl;

import com.jeongseonghun.urlshortener.shortening.validator.ValidationHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
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

    }

    @Override
    public void validate(String shortCode, HttpServletRequest request) throws ValidationException {

    }
}
