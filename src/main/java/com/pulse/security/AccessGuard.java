package com.pulse.security;

import com.pulse.entity.User;
import com.pulse.service.ExerciseService;
import com.pulse.service.ProgressService;
import com.pulse.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("accessGuard")
@Slf4j
@RequiredArgsConstructor
public class AccessGuard {
    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;
    private final ProgressService progressService;

    public boolean hasWorkoutAccess(Long workoutId, User user) {
        boolean allowed = user != null && workoutService.hasUser(workoutId, user.getId());
        log.debug("Workout access check workoutId={} userId={} allowed={}",
                workoutId, user != null ? user.getId() : null, allowed);
        return allowed;
    }

    public boolean hasExerciseWorkoutAccess(Long workoutId, User user) {
        boolean allowed = user != null && workoutService.hasUser(workoutId, user.getId());
        log.debug("Exercise-workout access check workoutId={} userId={} allowed={}",
                workoutId, user != null ? user.getId() : null, allowed);
        return allowed;
    }

    public boolean hasExerciseAccess(Long exerciseId, User user) {
        boolean allowed = user != null && exerciseService.hasUser(exerciseId, user.getId());
        log.debug("Exercise access check exerciseId={} userId={} allowed={}",
                exerciseId, user != null ? user.getId() : null, allowed);
        return allowed;
    }

    public boolean hasProgressAccess(Long progressId, User user) {
        boolean allowed = user != null && progressService.hasUser(progressId, user.getId());
        log.debug("Progress access check progressId={} userId={} allowed={}",
                progressId, user != null ? user.getId() : null, allowed);
        return allowed;
    }

    public boolean hasExerciseProgressAccess(Long exerciseId, User user) {
        boolean allowed = user != null && progressService.hasExerciseUser(exerciseId, user.getId());
        log.debug("Exercise-progress access check exerciseId={} userId={} allowed={}",
                exerciseId, user != null ? user.getId() : null, allowed);
        return allowed;
    }
}
