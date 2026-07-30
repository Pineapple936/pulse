package com.pulse.repository.exercise.type;

import com.pulse.jooq.tables.records.ExerciseTypeRecord;
import org.junit.jupiter.api.Test;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jooq.test.autoconfigure.JooqTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static com.pulse.jooq.tables.ExerciseType.EXERCISE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

@JooqTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ExerciseTypeRepository.class)
class ExerciseTypeRepositoryTest {

    @Autowired
    private DSLContext dsl;
    @Autowired
    private ExerciseTypeRepository repo;

    private Long typeId;

    @BeforeEach
    void insertType() {
        ExerciseTypeRecord type = new ExerciseTypeRecord();
        type.setName("Squat");
        type.setDescription("Barbell back squat");
        typeId = dsl.insertInto(EXERCISE_TYPE).set(type).returning(EXERCISE_TYPE.ID).fetchOne().getId();
    }

    @Test
    void existsById_returnsTrueForExistingType() {
        assertThat(repo.existsById(typeId)).isTrue();
    }

    @Test
    void existsById_returnsFalseForUnknownType() {
        assertThat(repo.existsById(-1L)).isFalse();
    }

    @Test
    void findByNameIgnoreCase_findsRegardlessOfCase() {
        Optional<ExerciseTypeRecord> found = repo.findByNameIgnoreCase("sQuAt");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(typeId);
    }

    @Test
    void findByNameIgnoreCase_emptyWhenNotFound() {
        assertThat(repo.findByNameIgnoreCase("deadlift")).isEmpty();
    }
}
