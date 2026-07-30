package com.pulse.repository.exercise;

import com.pulse.jooq.tables.records.ExerciseRecord;
import com.pulse.jooq.tables.records.ExerciseTypeRecord;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static com.pulse.jooq.tables.ExerciseType.EXERCISE_TYPE;
import static com.pulse.jooq.tables.User.USER;
import static com.pulse.jooq.tables.Workout.WORKOUT;
import static org.assertj.core.api.Assertions.assertThat;

@JooqTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ExerciseRepository.class)
class ExerciseRepositoryTest {

    @Autowired
    private DSLContext dsl;
    @Autowired
    private ExerciseRepository repo;

    private Long userId;
    private Long workoutId;
    private Long typeId;

    @BeforeEach
    void insertPrerequisites() {
        userId = insertUser("owner@example.com");
        workoutId = insertWorkout(userId);
        typeId = insertType();
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

    private ExerciseRecord newExercise() {
        ExerciseRecord exercise = new ExerciseRecord();
        exercise.setWorkoutId(workoutId);
        exercise.setExerciseTypeId(typeId);
        exercise.setDescription("3x10");
        return exercise;
    }

    @Test
    void save_generatesId() {
        ExerciseRecord saved = repo.save(newExercise());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getWorkoutId()).isEqualTo(workoutId);
    }

    @Test
    void findById_returnsSavedExercise() {
        ExerciseRecord saved = repo.save(newExercise());

        Optional<ExerciseRecord> found = repo.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("3x10");
    }

    @Test
    void findById_emptyWhenMissing() {
        assertThat(repo.findById(-1L)).isEmpty();
    }

    @Test
    void findByParent_returnsExercisesOfWorkout() {
        repo.save(newExercise());
        repo.save(newExercise());

        List<ExerciseRecord> result = repo.findByParent(workoutId, Page.of(0, 20));

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(e -> e.getWorkoutId().equals(workoutId));
    }

    @Test
    void deleteById_removesExercise() {
        ExerciseRecord saved = repo.save(newExercise());

        repo.deleteById(saved.getId());

        assertThat(repo.findById(saved.getId())).isEmpty();
    }

    @Test
    void hasAccess_trueForOwnerFalseForOthers() {
        ExerciseRecord saved = repo.save(newExercise());
        Long otherUser = insertUser("stranger@example.com");

        assertThat(repo.hasAccess(saved.getId(), userId)).isTrue();
        assertThat(repo.hasAccess(saved.getId(), otherUser)).isFalse();
    }
}
