package com.jeongseonghun.urlshortener.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OriginalUrlTest {

    @Test
    @DisplayName("유효한 URL로 OriginalUrl 객체를 생성한다.")
    void of_WithValidUrl() {
        // given
        String validUrl = "https://www.google.com/search?q=unit+test";

        // when
        OriginalUrl originalUrl = OriginalUrl.of(validUrl);

        // then
        assertThat(originalUrl).isNotNull();
        assertThat(originalUrl.getValue()).isEqualTo(validUrl);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-url",
            "ftp://example.com",
            "http:/example.com",
            "https//example.com"
    })
    @DisplayName("유효하지 않은 URL 형식이면 ValidationException을 던진다.")
    void of_WithInvalidUrlFormat(String invalidUrl) {
        // when & then
        assertThrows(ValidationException.class, () -> OriginalUrl.of(invalidUrl));
    }
}
