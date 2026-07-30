package com.pulse.controller.auth.entity.response;

public record AuthTokenResponse(
        String token,
        String refreshToken
) {
}
