package com.pulse.repository.exercise.type;

import com.pulse.jooq.tables.records.ExerciseTypeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.pulse.jooq.tables.ExerciseType.EXERCISE_TYPE;

@Repository
@RequiredArgsConstructor
public class ExerciseTypeRepository {
    private final DSLContext dsl;

    public Optional<ExerciseTypeRecord> findByNameIgnoreCase(String name) {
        return dsl.selectFrom(EXERCISE_TYPE)
                .where(EXERCISE_TYPE.NAME.equalIgnoreCase(name))
                .fetchOptional();
    }

    public boolean existsById(Long id) {
        return dsl.fetchExists(
                dsl.selectOne().from(EXERCISE_TYPE)
                        .where(EXERCISE_TYPE.ID.eq(id))
        );
    }
}
