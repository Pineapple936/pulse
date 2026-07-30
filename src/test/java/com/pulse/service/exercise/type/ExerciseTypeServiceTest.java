package com.pulse.service.exercise.type;

import com.pulse.jooq.tables.records.ExerciseTypeRecord;
import com.pulse.repository.exercise.type.ExerciseTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseTypeServiceTest {

    @Mock
    private ExerciseTypeRepository repo;

    @InjectMocks
    private ExerciseTypeService service;

    @Test
    void findByNameIgnoreCase_delegatesToRepository() {
        ExerciseTypeRecord record = new ExerciseTypeRecord(1L, "Squat", null, null);
        when(repo.findByNameIgnoreCase("squat")).thenReturn(Optional.of(record));

        Optional<ExerciseTypeRecord> result = service.findByNameIgnoreCase("squat");

        assertThat(result).containsSame(record);
    }

    @Test
    void existsById_returnsTrueWhenRepositoryConfirms() {
        when(repo.existsById(1L)).thenReturn(true);

        assertThat(service.existsById(1L)).isTrue();
    }

    @Test
    void existsById_returnsFalseWhenRepositoryDenies() {
        when(repo.existsById(1L)).thenReturn(false);

        assertThat(service.existsById(1L)).isFalse();
    }
}
