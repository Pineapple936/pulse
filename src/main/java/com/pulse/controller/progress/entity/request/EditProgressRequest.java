package com.pulse.controller.progress.entity.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record EditProgressRequest(
        @NotNull(message = "Id is required")
        @Positive(message = "Id must be a positive number")
        Long id,

        @Positive(message = "Repetitions must be a positive number")
        Integer repetition,

        @Positive(message = "Sets must be a positive number")
        Integer setNumber,

        @PositiveOrZero(message = "Weight cannot be negative")
        @Digits(integer = 4, fraction = 2, message = "Weight must have at most 4 integer and 2 fraction digits")
        BigDecimal weight
) {
}
