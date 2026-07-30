package com.pulse.repository.exercise;

import com.pulse.jooq.tables.records.ExerciseRecord;
import com.pulse.repository.interfaces.AccessRepository;
import com.pulse.repository.interfaces.FindableByParent;
import com.pulse.service.access.repository.entity.AccessResourceType;
import com.pulse.service.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.pulse.jooq.tables.Exercise.EXERCISE;
import static com.pulse.jooq.tables.Workout.WORKOUT;

@Repository
@RequiredArgsConstructor
public class ExerciseRepository implements FindableByParent<ExerciseRecord>, AccessRepository {
    private final DSLContext dsl;

    @Override
    public ExerciseRecord save(ExerciseRecord entity) {
        return dsl.insertInto(EXERCISE)
                .set(entity)
                .returning()
                .fetchOne();
    }

    @Override
    public Optional<ExerciseRecord> findById(Long id) {
        return dsl.selectFrom(EXERCISE)
                .where(EXERCISE.ID.eq(id))
                .fetchOptional();
    }

    @Override
    public List<ExerciseRecord> findByParent(Long workoutId, Page page) {
        return dsl.selectFrom(EXERCISE)
                .where(EXERCISE.WORKOUT_ID.eq(workoutId))
                .orderBy(EXERCISE.CREATED_AT)
                .limit(page.limit())
                .offset(page.offset())
                .fetch();
    }

    @Override
    public void deleteById(Long id) {
        dsl.deleteFrom(EXERCISE)
                .where(EXERCISE.ID.eq(id))
                .execute();
    }

    @Override
    public boolean hasAccess(Long id, Long userId) {
        return dsl.fetchExists(
                dsl.selectOne().from(EXERCISE)
                        .join(WORKOUT).on(WORKOUT.ID.eq(EXERCISE.WORKOUT_ID))
                        .where(EXERCISE.ID.eq(id))
                        .and(WORKOUT.USER_ID.eq(userId))
        );
    }

    @Override
    public AccessResourceType getType() {
        return AccessResourceType.EXERCISE;
    }
}
