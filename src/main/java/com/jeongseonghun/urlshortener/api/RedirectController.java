package com.jeongseonghun.urlshortener.api;

import com.jeongseonghun.urlshortener.domain.RedirectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * 단축 시킨 URL에 매핑된 원본 URL로 리다이렉트 해주기 위한 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final RedirectService redirectService;

    /**
     * 매핑된 원본 URL을 찾으면 HTTP 302 Found 상태 코드로 리다이렉트 응답을 보낸다.
     * @param shortKey 경로 변수(path variable)로 전달된 shortKey(우리 서비스에서 만든 key)
     * @param request  {@link HttpServletRequest}
     * @return 리다이렉션 정보를 담은 ResponseEntity 객체
     */
    @GetMapping("/{shortKey}")
    public ResponseEntity<Void> redirect(@PathVariable String shortKey, HttpServletRequest request) {
        String originalUrl = redirectService.getOriginalUrl(shortKey, request);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

}
