package com.pulse.entity.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDto(
        @NotBlank(message = "Token cannot be empty or blank")
        String refreshToken
) {
}
