package com.jeongseonghun.urlshortener.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 애플리케이션 전역 설정을 담는 클래스.
 * application.properties 파일의 'application' 접두사를 가진 값들이 자동으로 바인딩한다.
 */
@Configuration
@ConfigurationProperties(prefix = "application")
@Getter
public class AppProperties {
    /**
     * URL 단축 서비스의 Public 도메인 주소.
     * 설정 파일에 값이 명시되지 않은 경우 "localhost:8080"이 기본값으로 사용된다.
     */
    private final String domain = "localhost:8080";
}
