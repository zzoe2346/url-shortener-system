package com.jeongseonghun.urlshortener.shortening.controller;

import com.jeongseonghun.urlshortener.shortening.model.dto.ShortenRequest;
import com.jeongseonghun.urlshortener.shortening.model.dto.ShortenResponse;
import com.jeongseonghun.urlshortener.shortening.service.ShorteningService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
/**
 * URL 단축과 관련된 HTTP 요청을 처리하는 RESTFul 컨트롤러.
 */
@RestController
public class ShorteningController {

    private final ShorteningService shorteningService;

    public ShorteningController(ShorteningService shorteningService) {
        this.shorteningService = shorteningService;
    }


    /**
     * 원본 URL을 받아 단축 URL을 생성하고 반환.
     * <p>
     * 이미 단축된 적 있는 URL이라면 기존에 생성된 단축 URL을 반환한다.
     * 성공적으로 처리되면 HTTP 201 Created 상태 코드와 함께 생성된 단축 URL을 응답한다.
     * </p>
     * @param request 단축할 원본 URL을 담은 요청 DTO. {@code @Valid}를 통해 유효성 검사 수행함.
     * @return 생성된 단축 URL 키 정보를 담은 ResponseEntity 객체
     */
    @PostMapping("/urls")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        String shortenUrl = shorteningService.getOrCreateShortUrl(request.originalUrl());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ShortenResponse(shortenUrl));
    }

    /**
     * 단축 URL 키를 받아 원본 URL로 리다이렉트.
     * <p>
     * 매핑된 원본 URL을 찾으면 HTTP 302 Found 상태 코드로 리다이렉트 응답을 보낸다.
     * </p>
     * @param shortUrl 경로 변수(path variable)로 전달된 단축 URL
     * @return 리다이렉션 정보를 담은 ResponseEntity 객체
     */
    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl,HttpServletRequest request) {
        String originalUrl = shorteningService.getOriginalUrl(shortUrl, request);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

}
