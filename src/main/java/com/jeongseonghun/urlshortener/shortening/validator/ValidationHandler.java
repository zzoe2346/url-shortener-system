package com.jeongseonghun.urlshortener.shortening.validator;

import com.jeongseonghun.urlshortener.common.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 검증(Validation)용 Chain-Of-Responsibility 패턴 구현을 위한 인터페이스.
 */
public interface ValidationHandler {
    /**
     * 다음 검증이 필요한 요소를 세팅한다.
     *
     * @param next 다음 ValidationHandler 구현체
     */
    void setNext(ValidationHandler next);

    /**
     * URL 단축 로직용
     *
     * @param originalUrl 검증할 원본 URL
     * @throws ValidationException 검증 실패시 던지는 예외 객체
     */
    void validate(String originalUrl) throws ValidationException;

    /**
     * 리다이랙션 로직용
     *
     * @param shortCode 검증할 shortCode
     * @param request ip, referer 등 리다이랙션 요청한 클라이언트의 정보들을 담은 객체
     * @throws ValidationException 검증 실패시 던지는 예외 객체
     */
    void validate(String shortCode, HttpServletRequest request) throws ValidationException;
}
