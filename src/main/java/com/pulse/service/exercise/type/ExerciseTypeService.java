package com.pulse.service.exercise.type;

import com.pulse.jooq.tables.records.ExerciseTypeRecord;
import com.pulse.repository.exercise.type.ExerciseTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExerciseTypeService {
    private final ExerciseTypeRepository repo;

    public Optional<ExerciseTypeRecord> findByNameIgnoreCase(String name) {
        return repo.findByNameIgnoreCase(name);
    }

    public boolean existsById(Long id) {
        return repo.existsById(id);
    }
}
