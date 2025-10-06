package com.jeongseonghun.urlshortener.shortening.controller;

import com.jeongseonghun.urlshortener.shortening.model.dto.ShortenRequest;
import com.jeongseonghun.urlshortener.shortening.model.dto.ShortenResponse;
import com.jeongseonghun.urlshortener.shortening.service.ShorteningService;
import com.jeongseonghun.urlshortener.shortening.service.impl.ShorteningServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class ShorteningController {

    private final ShorteningService shorteningService;

    public ShorteningController(ShorteningService shorteningService) {
        this.shorteningService = shorteningService;
    }

    @PostMapping("/urls")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        String shortenUrl = shorteningService.shortenUrl(request.originalUrl());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ShortenResponse(shortenUrl));
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {
        String originalUrl = shorteningService.getOriginalUrl(shortUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

}
