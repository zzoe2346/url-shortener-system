package com.jeongseonghun.urlshortener.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ShortenRequest(@NotBlank String originalUrl) {
}
