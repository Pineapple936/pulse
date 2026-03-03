package com.pulse.service;

import com.pulse.entity.Exercise;
import com.pulse.entity.ExerciseType;
import com.pulse.entity.Workout;
import com.pulse.entity.dto.ExerciseDto;
import com.pulse.repository.ExerciseRepository;
import com.pulse.repository.ExerciseTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExerciseServiceTest {
    @Mock
    private ExerciseRepository repository;

    @Mock
    private ExerciseTypeRepository exerciseTypeRepository;

    @InjectMocks
    private ExerciseService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service.repository = repository;
    }

    @Test
    void save_shouldLookupTypeAndPersist() {
        Workout workout = new Workout();
        ExerciseDto dto = new ExerciseDto("push");
        ExerciseType type = new ExerciseType();
        when(exerciseTypeRepository.findByNameIgnoreCase("push")).thenReturn(Optional.of(type));

        service.save(workout, dto);
        verify(repository).save(argThat(e -> e.getWorkout() == workout && e.getExerciseType() == type));
    }

    @Test
    void save_shouldThrowWhenTypeMissing() {
        Workout workout = new Workout();
        ExerciseDto dto = new ExerciseDto("unknown");
        when(exerciseTypeRepository.findByNameIgnoreCase("unknown")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.save(workout, dto));
    }

    @Test
    void update_shouldChangeTypeAndSave() {
        Exercise ex = new Exercise();
        ex.setExerciseType(new ExerciseType());
        when(repository.findById(1L)).thenReturn(Optional.of(ex));
        when(repository.existsById(1L)).thenReturn(true);
        ExerciseType newType = new ExerciseType();
        when(exerciseTypeRepository.findByNameIgnoreCase("pull")).thenReturn(Optional.of(newType));

        service.update(1L, new ExerciseDto("pull"));
        assertSame(newType, ex.getExerciseType());
        verify(repository).save(ex);
    }

    @Test
    void update_shouldThrowWhenTypeMissing() {
        Exercise ex = new Exercise();
        when(repository.findById(1L)).thenReturn(Optional.of(ex));
        when(repository.existsById(1L)).thenReturn(true);
        when(exerciseTypeRepository.findByNameIgnoreCase("unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.update(1L, new ExerciseDto("unknown")));
        verify(repository, never()).save(any());
    }

    @Test
    void hasUser_forwardedToRepo() {
        when(repository.existsByIdAndWorkoutUserId(2L, 3L)).thenReturn(true);
        assertTrue(service.hasUser(2L, 3L));
        verify(repository).existsByIdAndWorkoutUserId(2L, 3L);
    }

    @Test
    void findExercisesByWorkoutId_delegatesToRepo() {
        when(repository.findExercisesByWorkoutId(7L)).thenReturn(java.util.Collections.emptyList());
        assertNotNull(service.findExercisesByWorkoutId(7L));
        verify(repository).findExercisesByWorkoutId(7L);
    }
}
