package com.pulse.service;

import com.pulse.entity.Exercise;
import com.pulse.entity.Progress;
import com.pulse.entity.dto.ProgressDetailsDto;
import com.pulse.repository.ProgressRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProgressServiceTest {
    @Mock
    private ProgressRepository repository;

    @InjectMocks
    private ProgressService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service.repository = repository;
    }

    @Test
    void findAllByExerciseId_delegates() {
        when(repository.findByExerciseId(10L)).thenReturn(Collections.emptyList());
        assertNotNull(service.findAllByExerciseId(10L));
        verify(repository).findByExerciseId(10L);
    }

    @Test
    void save_buildsEntity() {
        Exercise ex = new Exercise();
        ProgressDetailsDto dto = new ProgressDetailsDto(1,2,3.0f);
        service.save(ex, dto);
        verify(repository).save(any(Progress.class));
    }

    @Test
    void update_changesFields() {
        Progress prog = new Progress();
        prog.setRepetitions(1);
        prog.setSets(2);
        prog.setWeight(3.0f);
        when(repository.findById(1L)).thenReturn(Optional.of(prog));

        ProgressDetailsDto dto = new ProgressDetailsDto(5,2,7.0f);
        service.update(1L, dto);
        assertEquals(5, prog.getRepetitions());
        assertEquals(2, prog.getSets());
        assertEquals(7.0f, prog.getWeight());
        verify(repository).save(prog);
    }

    @Test
    void update_notFound_shouldThrowAndNotSave() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.update(1L, new ProgressDetailsDto(1, 1, 1.0f)));
        verify(repository, never()).save(any());
    }

    @Test
    void hasUser_delegates() {
        when(repository.existsByIdAndExerciseWorkoutUserId(2L,3L)).thenReturn(true);
        assertTrue(service.hasUser(2L,3L));
    }

    @Test
    void hasExerciseUser_delegates() {
        when(repository.existsExerciseByIdAndUserId(4L,5L)).thenReturn(true);
        assertTrue(service.hasExerciseUser(4L,5L));
    }
}
