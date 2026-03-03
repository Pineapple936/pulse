package com.pulse.service;

import com.pulse.entity.Exercise;
import com.pulse.entity.Progress;
import com.pulse.entity.dto.ProgressDetailsDto;
import com.pulse.entity.dto.ProgressUpdateDto;
import com.pulse.repository.ProgressRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
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
        List<Progress> expected = Collections.singletonList(new Progress());
        when(repository.findByExerciseId(10L)).thenReturn(expected);

        List<Progress> actual = service.findAllByExerciseId(10L);

        assertSame(expected, actual);
        verify(repository).findByExerciseId(10L);
    }

    @Test
    void save_buildsEntity() {
        Exercise ex = new Exercise();
        ex.setId(42L);
        ProgressDetailsDto dto = new ProgressDetailsDto(1,2,3.0f);

        service.save(ex, dto);

        ArgumentCaptor<Progress> captor = ArgumentCaptor.forClass(Progress.class);
        verify(repository).save(captor.capture());
        Progress saved = captor.getValue();
        assertEquals(1, saved.getRepetitions());
        assertEquals(2, saved.getSets());
        assertEquals(3.0f, saved.getWeight(), 0.0001f);
        assertSame(ex, saved.getExercise());
    }

    @Test
    void update_changesOnlyProvidedFields() {
        Progress prog = new Progress();
        prog.setRepetitions(1);
        prog.setSets(2);
        prog.setWeight(3.0f);
        when(repository.findById(1L)).thenReturn(Optional.of(prog));
        when(repository.existsById(1L)).thenReturn(true);

        ProgressUpdateDto dto = new ProgressUpdateDto(5, null, 7.0f);
        service.update(1L, dto);

        assertEquals(5, prog.getRepetitions());
        assertEquals(2, prog.getSets());
        assertEquals(7.0f, prog.getWeight(), 0.0001f);
        verify(repository).save(prog);
        verify(repository).existsById(1L);
    }

    @Test
    void update_withAllNullFields_keepsCurrentValues() {
        Progress prog = new Progress();
        prog.setRepetitions(10);
        prog.setSets(4);
        prog.setWeight(80.5f);
        when(repository.findById(1L)).thenReturn(Optional.of(prog));
        when(repository.existsById(1L)).thenReturn(true);

        service.update(1L, new ProgressUpdateDto(null, null, null));

        assertEquals(10, prog.getRepetitions());
        assertEquals(4, prog.getSets());
        assertEquals(80.5f, prog.getWeight(), 0.0001f);
        verify(repository).save(prog);
    }

    @Test
    void update_notFound_shouldThrowAndNotSave() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.update(1L, new ProgressUpdateDto(1, 1, 1.0f)));
        verify(repository, never()).save(any());
    }

    @Test
    void hasUser_delegates() {
        when(repository.existsByIdAndExerciseWorkoutUserId(2L,3L)).thenReturn(true);
        assertTrue(service.hasUser(2L,3L));
        verify(repository).existsByIdAndExerciseWorkoutUserId(2L,3L);
    }

    @Test
    void hasExerciseUser_delegates() {
        when(repository.existsExerciseByIdAndUserId(4L,5L)).thenReturn(true);
        assertTrue(service.hasExerciseUser(4L,5L));
        verify(repository).existsExerciseByIdAndUserId(4L,5L);
    }
}
