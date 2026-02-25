package com.example.demo.entity.dto.response;

import java.time.LocalDateTime;

public record ErrorResponseDto (
        String name,
        String message,
        LocalDateTime localDateTime
) {
}
