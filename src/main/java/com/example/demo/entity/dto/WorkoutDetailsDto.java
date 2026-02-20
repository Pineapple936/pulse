package com.example.demo.entity.dto;

import java.time.LocalDateTime;

public record WorkoutDetailsDto(
        String name,
        LocalDateTime date
) {
}
