package com.pulse.repository.user;

import com.pulse.jooq.tables.records.UserRecord;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jooq.test.autoconfigure.JooqTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JooqTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserRepository.class)
class UserRepositoryTest {

    @Autowired
    private DSLContext dsl;
    @Autowired
    private UserRepository repo;

    private UserRecord newUser(String email) {
        UserRecord user = new UserRecord();
        user.setName("John");
        user.setEmail(email);
        user.setPassword("hash");
        return user;
    }

    @Test
    void save_generatesIdAndCreatedAt() {
        UserRecord saved = repo.save(newUser("save@example.com"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("save@example.com");
    }

    @Test
    void findById_returnsSavedUser() {
        UserRecord saved = repo.save(newUser("findid@example.com"));

        Optional<UserRecord> found = repo.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("findid@example.com");
    }

    @Test
    void findById_emptyWhenMissing() {
        assertThat(repo.findById(-1L)).isEmpty();
    }

    @Test
    void findByEmail_returnsSavedUser() {
        repo.save(newUser("byemail@example.com"));

        Optional<UserRecord> found = repo.findByEmail("byemail@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John");
    }

    @Test
    void findByEmail_emptyWhenUnknown() {
        assertThat(repo.findByEmail("nobody@example.com")).isEmpty();
    }

    @Test
    void deleteById_removesUser() {
        UserRecord saved = repo.save(newUser("delete@example.com"));

        repo.deleteById(saved.getId());

        assertThat(repo.findById(saved.getId())).isEmpty();
    }
}
