package com.pulse.controller;

import com.pulse.entity.Exercise;
import com.pulse.entity.User;
import com.pulse.entity.Workout;
import com.pulse.entity.dto.ExerciseDto;
import com.pulse.entity.dto.response.ResponseMessageDto;
import com.pulse.service.ExerciseService;
import com.pulse.service.WorkoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExerciseControllerTest {

    @Mock
    private ExerciseService exerciseService;

    @Mock
    private WorkoutService workoutService;

    @InjectMocks
    private ExerciseController controller;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
    }

    @Test
    void findAllExercisesByWorkout_authorized() {
        Long workoutId = 5L;
        List<Exercise> exercises = Collections.singletonList(new Exercise());

        when(exerciseService.findExercisesByWorkoutId(workoutId)).thenReturn(exercises);

        ResponseEntity<?> resp = controller.findAllExercisesByWorkout(workoutId, user);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(exercises, resp.getBody());
        verify(exerciseService).findExercisesByWorkoutId(workoutId);
        verifyNoInteractions(workoutService);
    }

    @Test
    void createExerciseInWorkout_authorized() {
        Long workoutId = 2L;
        ExerciseDto dto = new ExerciseDto("push");
        Workout workout = new Workout();

        when(workoutService.findById(workoutId)).thenReturn(workout);

        ResponseEntity<ResponseMessageDto> resp = controller.createExerciseInWorkout(workoutId, dto, user);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        verify(exerciseService).save(workout, dto);
    }

    @Test
    void editExerciseById_authorized() {
        Long exerciseId = 10L;
        ExerciseDto dto = new ExerciseDto("a");

        ResponseEntity<ResponseMessageDto> resp = controller.editExerciseById(exerciseId, dto, user);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(exerciseService).update(exerciseId, dto);
    }

    @Test
    void deleteExerciseById_authorized() {
        Long exerciseId = 20L;

        ResponseEntity<ResponseMessageDto> resp = controller.deleteExerciseById(exerciseId, user);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(exerciseService).delete(exerciseId);
    }
}
