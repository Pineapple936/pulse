package com.pulse.controller.workout.entity.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record EditWorkoutRequest (
        @NotNull(message = "Id is required")
        @Positive(message = "Id must be a positive number")
        Long id,

        @Size(max = 30, message = "Name cannot be longer than 30 characters")
        String name,

        @PastOrPresent(message = "Workout date cannot be in the future")
        OffsetDateTime performedAt
) {
}
