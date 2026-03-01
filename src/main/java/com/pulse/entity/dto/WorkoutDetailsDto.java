package com.pulse.entity.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record WorkoutDetailsDto(
        @Size(max = 30, message = "Name cannot be longer than 30 characters")
        String name,

        LocalDateTime date
) {
}
