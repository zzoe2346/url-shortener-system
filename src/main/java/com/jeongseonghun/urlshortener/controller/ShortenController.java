package com.jeongseonghun.urlshortener.controller;

import com.jeongseonghun.urlshortener.dto.ShortenRequest;
import com.jeongseonghun.urlshortener.dto.ShortenResponse;
import com.jeongseonghun.urlshortener.service.ShortenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class ShortenController {

    private final ShortenService shortenService;

    public ShortenController(ShortenService shortenService) {
        this.shortenService = shortenService;
    }

    @PostMapping("/urls")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        String shortenUrl = shortenService.shortenUrl(request.originalUrl());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ShortenResponse(shortenUrl));
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {
        String originalUrl = shortenService.getOriginalUrl(shortUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

}
