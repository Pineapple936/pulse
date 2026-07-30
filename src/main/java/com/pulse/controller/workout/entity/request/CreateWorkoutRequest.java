package com.pulse.controller.workout.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record CreateWorkoutRequest (
        @NotBlank(message = "Name cannot be empty or blank")
        @Size(max = 30, message = "Name cannot be longer than 30 characters")
        String name,

        @NotNull(message = "Workout date is required")
        @PastOrPresent(message = "Workout date cannot be in the future")
        OffsetDateTime performedAt
) {
}
