package com.pulse.entity.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProgressDetailsDto(
        @NotNull(message = "Repetitions cannot be null")
        @Positive(message = "Repetitions must be a positive number")
        int repetitions,

        @NotNull(message = "Sets cannot be null")
        @Positive(message = "Sets must be a positive number")
        int sets,

        @NotNull(message = "Weight cannot be null")
        @Positive(message = "Weight must be a positive number")
        float weight
) {
}
