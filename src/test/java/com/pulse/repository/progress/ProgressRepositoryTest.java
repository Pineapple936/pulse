package com.pulse.repository.progress;

import com.pulse.jooq.tables.records.ExerciseRecord;
import com.pulse.jooq.tables.records.ExerciseTypeRecord;
import com.pulse.jooq.tables.records.ProgressRecord;
import com.pulse.jooq.tables.records.UserRecord;
import com.pulse.jooq.tables.records.WorkoutRecord;
import com.pulse.service.pagination.Page;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jooq.test.autoconfigure.JooqTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static com.pulse.jooq.tables.Exercise.EXERCISE;
import static com.pulse.jooq.tables.ExerciseType.EXERCISE_TYPE;
import static com.pulse.jooq.tables.User.USER;
import static com.pulse.jooq.tables.Workout.WORKOUT;
import static org.assertj.core.api.Assertions.assertThat;

@JooqTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ProgressRepository.class)
class ProgressRepositoryTest {

    @Autowired
    private DSLContext dsl;
    @Autowired
    private ProgressRepository repo;

    private Long userId;
    private Long exerciseId;

    @BeforeEach
    void insertPrerequisites() {
        userId = insertUser("owner@example.com");
        Long workoutId = insertWorkout(userId);
        Long typeId = insertType();
        exerciseId = insertExercise(workoutId, typeId);
    }

    private Long insertUser(String email) {
        UserRecord user = new UserRecord();
        user.setName("John");
        user.setEmail(email);
        user.setPassword("hash");
        return dsl.insertInto(USER).set(user).returning(USER.ID).fetchOne().getId();
    }

    private Long insertWorkout(Long ownerId) {
        WorkoutRecord workout = new WorkoutRecord();
        workout.setName("Leg day");
        workout.setPerformedAt(OffsetDateTime.now());
        workout.setUserId(ownerId);
        return dsl.insertInto(WORKOUT).set(workout).returning(WORKOUT.ID).fetchOne().getId();
    }

    private Long insertType() {
        ExerciseTypeRecord type = new ExerciseTypeRecord();
        type.setName("Squat");
        return dsl.insertInto(EXERCISE_TYPE).set(type).returning(EXERCISE_TYPE.ID).fetchOne().getId();
    }

    private Long insertExercise(Long workoutId, Long typeId) {
        ExerciseRecord exercise = new ExerciseRecord();
        exercise.setWorkoutId(workoutId);
        exercise.setExerciseTypeId(typeId);
        return dsl.insertInto(EXERCISE).set(exercise).returning(EXERCISE.ID).fetchOne().getId();
    }

    private ProgressRecord newProgress(int setNumber) {
        ProgressRecord progress = new ProgressRecord();
        progress.setExerciseId(exerciseId);
        progress.setSetNumber(setNumber);
        progress.setRepetition(10);
        progress.setWeight(new BigDecimal("50.00"));
        return progress;
    }

    @Test
    void save_generatesId() {
        ProgressRecord saved = repo.save(newProgress(1));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getExerciseId()).isEqualTo(exerciseId);
    }

    @Test
    void findById_returnsSavedProgress() {
        ProgressRecord saved = repo.save(newProgress(1));

        Optional<ProgressRecord> found = repo.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getRepetition()).isEqualTo(10);
    }

    @Test
    void findById_emptyWhenMissing() {
        assertThat(repo.findById(-1L)).isEmpty();
    }

    @Test
    void findByParent_returnsProgressOfExercise() {
        repo.save(newProgress(1));
        repo.save(newProgress(2));

        List<ProgressRecord> result = repo.findByParent(exerciseId, Page.of(0, 20));

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p -> p.getExerciseId().equals(exerciseId));
    }

    @Test
    void deleteById_removesProgress() {
        ProgressRecord saved = repo.save(newProgress(1));

        repo.deleteById(saved.getId());

        assertThat(repo.findById(saved.getId())).isEmpty();
    }

    @Test
    void hasAccess_trueForOwnerFalseForOthers() {
        ProgressRecord saved = repo.save(newProgress(1));
        Long otherUser = insertUser("stranger@example.com");

        assertThat(repo.hasAccess(saved.getId(), userId)).isTrue();
        assertThat(repo.hasAccess(saved.getId(), otherUser)).isFalse();
    }
}
