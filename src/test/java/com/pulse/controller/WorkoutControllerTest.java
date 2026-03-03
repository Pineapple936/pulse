package com.pulse.controller;

import com.pulse.entity.User;
import com.pulse.entity.Workout;
import com.pulse.entity.dto.WorkoutDetailsDto;
import com.pulse.entity.dto.response.ResponseMessageDto;
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

class WorkoutControllerTest {

    @Mock
    private WorkoutService workoutService;

    @InjectMocks
    private WorkoutController controller;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
    }

    @Test
    void getAllWorkouts_shouldReturnList() {
        List<Workout> list = Collections.singletonList(new Workout());
        when(workoutService.getAllWorkoutsByUser(user.getId())).thenReturn(list);

        ResponseEntity<List<Workout>> resp = controller.getAllWorkouts(user);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(list, resp.getBody());
    }

    @Test
    void findWorkoutById_authorized() {
        Long id = 9L;
        Workout workout = new Workout();
        when(workoutService.findById(id)).thenReturn(workout);

        ResponseEntity<?> resp = controller.findWorkoutById(id, user);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(workout, resp.getBody());
    }

    @Test
    void createWorkout() {
        WorkoutDetailsDto dto = new WorkoutDetailsDto("name", null);
        ResponseEntity<ResponseMessageDto> resp = controller.createWorkout(dto, user);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        verify(workoutService).save(dto, user);
    }

    @Test
    void editWorkoutById_authorized() {
        Long id = 15L;
        WorkoutDetailsDto dto = new WorkoutDetailsDto("a", null);

        ResponseEntity<ResponseMessageDto> resp = controller.editWorkoutById(id, dto, user);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(workoutService).update(id, dto);
    }

    @Test
    void deleteWorkoutById_authorized() {
        Long id = 7L;
        ResponseEntity<ResponseMessageDto> resp = controller.deleteWorkoutById(id, user);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(workoutService).delete(id);
    }
}
