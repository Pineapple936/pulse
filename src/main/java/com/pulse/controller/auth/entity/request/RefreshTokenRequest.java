package com.pulse.controller.auth.entity.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Token cannot be empty or blank")
        String refreshToken
) {
}
