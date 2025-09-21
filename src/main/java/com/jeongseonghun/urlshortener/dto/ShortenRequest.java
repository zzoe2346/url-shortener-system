package com.jeongseonghun.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ShortenRequest(
        @NotBlank(message = "URL은 비어 있을 수 없습니다.")
        @Pattern(
                regexp = "^(https?://).+",
                message = "URL은 http:// 또는 https:// 로 시작해야 합니다."
        )
        String originalUrl
) {
}
