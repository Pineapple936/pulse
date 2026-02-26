package com.pulse.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record WorkoutDetailsDto(
        @NotBlank(message = "Name cannot be empty or blank")
        @Size(max = 30, message = "Name cannot be longer than 30 characters")
        String name,

        @NotNull(message = "Date cannot be null")
        LocalDateTime date
) {
}
