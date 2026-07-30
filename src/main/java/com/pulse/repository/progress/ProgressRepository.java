package com.pulse.repository.progress;

import com.pulse.jooq.tables.records.ProgressRecord;
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
import static com.pulse.jooq.tables.Progress.PROGRESS;
import static com.pulse.jooq.tables.Workout.WORKOUT;

@Repository
@RequiredArgsConstructor
public class ProgressRepository implements FindableByParent<ProgressRecord>, AccessRepository {
    private final DSLContext dsl;

    @Override
    public ProgressRecord save(ProgressRecord entity) {
        return dsl.insertInto(PROGRESS)
                .set(entity)
                .returning()
                .fetchOne();
    }

    @Override
    public Optional<ProgressRecord> findById(Long id) {
        return dsl.selectFrom(PROGRESS)
                .where(PROGRESS.ID.eq(id))
                .fetchOptional();
    }

    @Override
    public List<ProgressRecord> findByParent(Long exerciseId, Page page) {
        return dsl.selectFrom(PROGRESS)
                .where(PROGRESS.EXERCISE_ID.eq(exerciseId))
                .orderBy(PROGRESS.ID)
                .limit(page.limit())
                .offset(page.offset())
                .fetch();
    }

    @Override
    public void deleteById(Long id) {
        dsl.deleteFrom(PROGRESS)
                .where(PROGRESS.ID.eq(id))
                .execute();
    }

    @Override
    public boolean hasAccess(Long id, Long userId) {
        return dsl.fetchExists(
                dsl.selectOne().from(PROGRESS)
                        .join(EXERCISE).on(EXERCISE.ID.eq(PROGRESS.EXERCISE_ID))
                        .join(WORKOUT).on(WORKOUT.ID.eq(EXERCISE.WORKOUT_ID))
                        .where(PROGRESS.ID.eq(id).and(WORKOUT.USER_ID.eq(userId)))
        );
    }

    @Override
    public AccessResourceType getType() {
        return AccessResourceType.PROGRESS;
    }
}
