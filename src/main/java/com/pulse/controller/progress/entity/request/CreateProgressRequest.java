package com.pulse.controller.progress.entity.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateProgressRequest(
        @NotNull(message = "Exercise id is required")
        @Positive(message = "Exercise id must be a positive number")
        Long exerciseId,

        @NotNull(message = "Sets are required")
        @Positive(message = "Sets must be a positive number")
        Integer setNumber,

        @NotNull(message = "Repetitions are required")
        @Positive(message = "Repetitions must be a positive number")
        Integer repetition,

        @PositiveOrZero(message = "Weight cannot be negative")
        @Digits(integer = 4, fraction = 2, message = "Weight must have at most 4 integer and 2 fraction digits")
        BigDecimal weight
) {
}
