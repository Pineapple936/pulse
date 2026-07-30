package com.pulse.controller.exercise.entity.response;

public record ExerciseResponse(
        Long id,
        Long workoutId,
        Long exerciseTypeId,
        String description
) {
}
