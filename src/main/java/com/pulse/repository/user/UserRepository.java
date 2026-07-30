package com.pulse.repository.user;

import com.pulse.jooq.tables.records.UserRecord;
import com.pulse.repository.interfaces.EntityRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.pulse.jooq.tables.User.USER;

@Repository
@RequiredArgsConstructor
public class UserRepository implements EntityRepository<UserRecord> {
    private final DSLContext dsl;

    @Override
    public UserRecord save(UserRecord entity) {
        return dsl.insertInto(USER)
                .set(entity)
                .returning()
                .fetchOne();

    }

    @Override
    public Optional<UserRecord> findById(Long id) {
        return dsl.selectFrom(USER)
                .where(USER.ID.eq(id))
                .fetchOptional();
    }

    public Optional<UserRecord> findByEmail(String email) {
        return dsl.selectFrom(USER)
                .where(USER.EMAIL.eq(email))
                .fetchOptional();
    }

    @Override
    public void deleteById(Long id) {
        dsl.deleteFrom(USER)
                .where(USER.ID.eq(id))
                .execute();
    }
}
