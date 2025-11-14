package com.jeongseonghun.urlshortener.domain.validator;

import com.jeongseonghun.urlshortener.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DomainBlacklistHandler implements ValidationHandler {
    private final Set<String> blacklist = Set.of("BAD.com", "불법.net");

    private ValidationHandler next;

    @Override
    public void setNext(ValidationHandler next) {
        this.next = next;
    }

    @Override
    public void validate(String originalUrl) throws ValidationException {
        for (String blacklisted : blacklist) {
            if (originalUrl.contains(blacklisted)) {
                throw new ValidationException("URL is blacklisted: " + blacklisted);
            }
        }
        if (next != null) {
            next.validate(originalUrl);
        }
    }

    @Override
    public void validate(String shortCode, HttpServletRequest request) throws ValidationException {

    }
}