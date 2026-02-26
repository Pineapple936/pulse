package com.pulse.entity.dto.auth;

public record JWTAuthentificationDto(
        String token,
        String refreshToken
) {
}
