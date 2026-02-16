package com.example.demo.entity.dto;

public record JWTAuthentificationDto(
        String token,
        String refreshToken
) {
}
