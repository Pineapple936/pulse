package com.pulse.repository.workout;

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

import static com.pulse.jooq.tables.User.USER;
import static org.assertj.core.api.Assertions.assertThat;

@JooqTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(WorkoutRepository.class)
class WorkoutRepositoryTest {

    @Autowired
    private DSLContext dsl;
    @Autowired
    private WorkoutRepository repo;

    private Long userId;

    @BeforeEach
    void insertUser() {
        userId = insertUser("owner@example.com");
    }

    private Long insertUser(String email) {
        UserRecord user = new UserRecord();
        user.setName("John");
        user.setEmail(email);
        user.setPassword("hash");
        return dsl.insertInto(USER).set(user).returning(USER.ID).fetchOne().getId();
    }

    private WorkoutRecord newWorkout(String name) {
        WorkoutRecord workout = new WorkoutRecord();
        workout.setName(name);
        workout.setPerformedAt(OffsetDateTime.now());
        workout.setUserId(userId);
        return workout;
    }

    @Test
    void save_generatesId() {
        WorkoutRecord saved = repo.save(newWorkout("Leg day"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(userId);
    }

    @Test
    void findById_returnsSavedWorkout() {
        WorkoutRecord saved = repo.save(newWorkout("Leg day"));

        Optional<WorkoutRecord> found = repo.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Leg day");
    }

    @Test
    void findById_emptyWhenMissing() {
        assertThat(repo.findById(-1L)).isEmpty();
    }

    @Test
    void findByParent_returnsOnlyUsersWorkouts() {
        repo.save(newWorkout("Leg day"));
        repo.save(newWorkout("Push day"));
        Long otherUser = insertUser("other@example.com");
        WorkoutRecord otherWorkout = new WorkoutRecord();
        otherWorkout.setName("Not mine");
        otherWorkout.setPerformedAt(OffsetDateTime.now());
        otherWorkout.setUserId(otherUser);
        repo.save(otherWorkout);

        List<WorkoutRecord> result = repo.findByParent(userId, Page.of(0, 20));

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(w -> w.getUserId().equals(userId));
    }

    @Test
    void deleteById_removesWorkout() {
        WorkoutRecord saved = repo.save(newWorkout("Leg day"));

        repo.deleteById(saved.getId());

        assertThat(repo.findById(saved.getId())).isEmpty();
    }

    @Test
    void hasAccess_trueForOwnerFalseForOthers() {
        WorkoutRecord saved = repo.save(newWorkout("Leg day"));
        Long otherUser = insertUser("stranger@example.com");

        assertThat(repo.hasAccess(saved.getId(), userId)).isTrue();
        assertThat(repo.hasAccess(saved.getId(), otherUser)).isFalse();
    }
}
