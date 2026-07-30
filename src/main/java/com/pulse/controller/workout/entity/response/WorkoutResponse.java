package com.pulse.controller.workout.entity.response;

import java.time.OffsetDateTime;

public record WorkoutResponse(
        Long id,
        Long userId,
        String name,
        OffsetDateTime performedAt
) {
}
