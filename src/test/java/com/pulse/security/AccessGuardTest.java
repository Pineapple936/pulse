package com.pulse.security;

import com.pulse.entity.User;
import com.pulse.service.ExerciseService;
import com.pulse.service.ProgressService;
import com.pulse.service.WorkoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccessGuardTest {

    @Mock
    private WorkoutService workoutService;

    @Mock
    private ExerciseService exerciseService;

    @Mock
    private ProgressService progressService;

    private AccessGuard accessGuard;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accessGuard = new AccessGuard(workoutService, exerciseService, progressService);
    }

    @Test
    void hasWorkoutAccess_shouldDelegateToWorkoutService() {
        User user = new User();
        user.setId(1L);
        when(workoutService.hasUser(10L, 1L)).thenReturn(true);

        assertTrue(accessGuard.hasWorkoutAccess(10L, user));
        verify(workoutService).hasUser(10L, 1L);
    }

    @Test
    void hasWorkoutAccess_nullUser_shouldReturnFalse() {
        assertFalse(accessGuard.hasWorkoutAccess(10L, null));
        verifyNoInteractions(workoutService);
    }

    @Test
    void hasExerciseWorkoutAccess_shouldDelegateToExerciseService() {
        User user = new User();
        user.setId(2L);
        when(exerciseService.hasWorkoutUser(11L, 2L)).thenReturn(true);

        assertTrue(accessGuard.hasExerciseWorkoutAccess(11L, user));
        verify(exerciseService).hasWorkoutUser(11L, 2L);
    }

    @Test
    void hasExerciseAccess_shouldDelegateToExerciseService() {
        User user = new User();
        user.setId(3L);
        when(exerciseService.hasUser(12L, 3L)).thenReturn(true);

        assertTrue(accessGuard.hasExerciseAccess(12L, user));
        verify(exerciseService).hasUser(12L, 3L);
    }

    @Test
    void hasProgressAccess_shouldDelegateToProgressService() {
        User user = new User();
        user.setId(4L);
        when(progressService.hasUser(13L, 4L)).thenReturn(true);

        assertTrue(accessGuard.hasProgressAccess(13L, user));
        verify(progressService).hasUser(13L, 4L);
    }

    @Test
    void hasExerciseProgressAccess_shouldDelegateToProgressService() {
        User user = new User();
        user.setId(5L);
        when(progressService.hasExerciseUser(14L, 5L)).thenReturn(true);

        assertTrue(accessGuard.hasExerciseProgressAccess(14L, user));
        verify(progressService).hasExerciseUser(14L, 5L);
    }
}
