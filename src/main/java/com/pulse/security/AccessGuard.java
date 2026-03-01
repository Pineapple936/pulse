package com.pulse.security;

import com.pulse.entity.User;
import com.pulse.service.ExerciseService;
import com.pulse.service.ProgressService;
import com.pulse.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("accessGuard")
@RequiredArgsConstructor
public class AccessGuard {
    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;
    private final ProgressService progressService;

    public boolean hasWorkoutAccess(Long workoutId, User user) {
        return user != null && workoutService.hasUser(workoutId, user.getId());
    }

    public boolean hasExerciseWorkoutAccess(Long workoutId, User user) {
        return user != null && exerciseService.hasWorkoutUser(workoutId, user.getId());
    }

    public boolean hasExerciseAccess(Long exerciseId, User user) {
        return user != null && exerciseService.hasUser(exerciseId, user.getId());
    }

    public boolean hasProgressAccess(Long progressId, User user) {
        return user != null && progressService.hasUser(progressId, user.getId());
    }

    public boolean hasExerciseProgressAccess(Long exerciseId, User user) {
        return user != null && progressService.hasExerciseUser(exerciseId, user.getId());
    }
}
