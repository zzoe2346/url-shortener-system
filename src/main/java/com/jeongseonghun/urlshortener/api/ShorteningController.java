package com.jeongseonghun.urlshortener.api;

import com.jeongseonghun.urlshortener.api.dto.ShortUrlResponse;
import com.jeongseonghun.urlshortener.api.dto.ShortenRequest;
import com.jeongseonghun.urlshortener.domain.ShorteningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * URL 단축과 관련된 HTTP 요청을 처리하는 RESTFul 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
public class ShorteningController {

    private final ShorteningService shorteningService;

    /**
     * 원본 URL을 받아 단축 URL을 생성하고 반환.
     * <p>
     * 이미 단축된 적 있는 URL이라면 기존에 생성된 단축 URL을 반환한다.
     * 성공적으로 처리되면 HTTP 201 Created 상태 코드와 함께 생성된 단축 URL을 응답한다.
     * </p>
     *
     * @param request 단축할 원본 URL을 담은 요청 DTO. {@link  Valid}를 통해 유효성 검사 수행함.
     * @return 생성된 단축 URL 키 정보를 담은 ResponseEntity 객체
     */
    @PostMapping("/urls")
    public ResponseEntity<ShortUrlResponse> shorten(@RequestBody ShortenRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(shorteningService.getOrCreateShortUrl(request.originalUrl()));
    }

}
