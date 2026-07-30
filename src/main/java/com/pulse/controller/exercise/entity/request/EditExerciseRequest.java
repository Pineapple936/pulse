package com.pulse.controller.exercise.entity.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EditExerciseRequest(
        @NotNull(message = "Id is required")
        @Positive(message = "Id must be a positive number")
        Long id,

        @Positive(message = "Exercise type id must be a positive number")
        Long exerciseTypeId,

        @Size(max = 50, message = "Description cannot be longer than 50 characters")
        String description
) {
}
