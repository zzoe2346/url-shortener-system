package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.ValidationHandler;
import com.jeongseonghun.urlshortener.domain.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class IpBlacklistHandler implements ValidationHandler {
    private final Set<String> ipBlacklist = new HashSet<>();

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
        String ip = request.getRemoteAddr();
        if (ipBlacklist.contains(ip)) {
            throw new ValidationException("Request from blacklisted IP: " + ip);
        }
        if (next != null) {
            next.validate(shortCode, request);
        }
    }
}