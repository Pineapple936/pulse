package com.example.demo.entity.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDto(
        @NotBlank(message = "Token cannot be empty or blank")
        String refreshToken
) {
}
