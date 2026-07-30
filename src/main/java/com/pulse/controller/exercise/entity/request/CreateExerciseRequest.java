package com.pulse.controller.exercise.entity.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateExerciseRequest(
        @NotNull(message = "Workout id is required")
        @Positive(message = "Workout id must be a positive number")
        Long workoutId,

        @NotNull(message = "Exercise type id is required")
        @Positive(message = "Exercise type id must be a positive number")
        Long exerciseTypeId,

        @Size(max = 50, message = "Description cannot be longer than 50 characters")
        String description
) {
}
