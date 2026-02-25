package com.example.demo.entity.dto.auth;

public record JWTAuthentificationDto(
        String token,
        String refreshToken
) {
}
