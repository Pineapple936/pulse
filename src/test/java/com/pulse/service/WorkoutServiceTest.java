package com.pulse.service;

import com.pulse.entity.User;
import com.pulse.entity.Workout;
import com.pulse.entity.dto.WorkoutDetailsDto;
import com.pulse.repository.WorkoutRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkoutServiceTest {

    @Mock
    private WorkoutRepository repository;

    @InjectMocks
    private WorkoutService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service.repository = repository;
    }

    @Test
    void getAllWorkoutsByUser_shouldCallRepo() {
        when(repository.findAllByUserId(1L)).thenReturn(Collections.emptyList());
        assertNotNull(service.getAllWorkoutsByUser(1L));
        verify(repository).findAllByUserId(1L);
    }

    @Test
    void save_buildsEntityAndPersists() {
        User user = new User();
        user.setId(2L);
        WorkoutDetailsDto dto = new WorkoutDetailsDto("foo", LocalDateTime.now());
        service.save(dto, user);
        verify(repository).save(any(Workout.class));
    }

    @Test
    void update_shouldModifyFields() {
        Workout existing = new Workout();
        existing.setName("old");
        existing.setDate(LocalDateTime.of(2020,1,1,0,0));
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.existsByIdAndUserId(anyLong(), anyLong())).thenReturn(true);

        WorkoutDetailsDto dto = new WorkoutDetailsDto("new", LocalDateTime.of(2021,1,1,0,0));
        service.update(1L, dto);
        assertEquals("new", existing.getName());
        assertEquals(LocalDateTime.of(2021,1,1,0,0), existing.getDate());
        verify(repository).save(existing);
    }

    @Test
    void update_shouldIgnoreNullFields() {
        Workout existing = new Workout();
        existing.setName("old");
        existing.setDate(LocalDateTime.of(2020, 1, 1, 0, 0));
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsById(1L)).thenReturn(true);

        service.update(1L, new WorkoutDetailsDto(null, null));

        assertEquals("old", existing.getName());
        assertEquals(LocalDateTime.of(2020, 1, 1, 0, 0), existing.getDate());
        verify(repository).save(existing);
    }

    @Test
    void update_notFound_shouldThrowAndNotSave() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.update(1L, new WorkoutDetailsDto("new", LocalDateTime.now())));
        verify(repository, never()).save(any());
    }

    @Test
    void hasUser_forwardsToRepo() {
        when(repository.existsByIdAndUserId(5L, 10L)).thenReturn(true);
        assertTrue(service.hasUser(5L, 10L));
        verify(repository).existsByIdAndUserId(5L, 10L);
    }
}
