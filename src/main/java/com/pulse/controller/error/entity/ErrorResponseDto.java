package com.pulse.controller.error.entity;

import java.time.LocalDateTime;

public record ErrorResponseDto (
        String name,
        String message,
        LocalDateTime localDateTime
) {
}
