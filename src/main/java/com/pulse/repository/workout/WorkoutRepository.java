package com.pulse.repository.workout;

import com.pulse.jooq.tables.records.WorkoutRecord;
import com.pulse.repository.interfaces.AccessRepository;
import com.pulse.repository.interfaces.FindableByParent;
import com.pulse.service.access.repository.entity.AccessResourceType;
import com.pulse.service.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.pulse.jooq.tables.User.USER;
import static com.pulse.jooq.tables.Workout.WORKOUT;

@Repository
@RequiredArgsConstructor
public class WorkoutRepository implements FindableByParent<WorkoutRecord>, AccessRepository {
    private final DSLContext dsl;

    @Override
    public WorkoutRecord save(WorkoutRecord entity) {
        return dsl.insertInto(WORKOUT)
                .set(entity)
                .returning()
                .fetchOne();
    }

    @Override
    public void deleteById(Long id) {
        dsl.deleteFrom(WORKOUT)
                .where(WORKOUT.ID.eq(id))
                .execute();
    }

    @Override
    public Optional<WorkoutRecord> findById(Long id) {
        return dsl.selectFrom(WORKOUT)
                .where(WORKOUT.ID.eq(id))
                .fetchOptional();
    }

    @Override
    public List<WorkoutRecord> findByParent(Long userId, Page page) {
        return dsl.selectFrom(WORKOUT)
                .where(WORKOUT.USER_ID.eq(userId))
                .orderBy(WORKOUT.PERFORMED_AT.desc())
                .limit(page.limit())
                .offset(page.offset())
                .fetch();
    }

    @Override
    public boolean hasAccess(Long id, Long userId) {
        return dsl.fetchExists(
                dsl.selectOne().from(WORKOUT)
                        .join(USER).on(WORKOUT.USER_ID.eq(USER.ID))
                        .where(USER.ID.eq(userId).and(WORKOUT.ID.eq(id)))
        );
    }

    @Override
    public AccessResourceType getType() {
        return AccessResourceType.WORKOUT;
    }
}
