package com.pulse.entity.dto;

import jakarta.validation.constraints.Positive;

public record ProgressUpdateDto(
        @Positive(message = "Repetitions must be a positive number")
        Integer repetitions,

        @Positive(message = "Sets must be a positive number")
        Integer sets,

        @Positive(message = "Weight must be a positive number")
        Float weight
) {
}
