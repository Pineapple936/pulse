package com.pulse.repository.user.entity;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    private static final OffsetDateTime CREATED_AT = OffsetDateTime.parse("2026-01-01T10:00:00Z");

    private UserDetailsImpl user() {
        return new UserDetailsImpl(1L, "John", "john@example.com", "hash", CREATED_AT);
    }

    @Test
    void exposesStoredFields() {
        UserDetailsImpl user = user();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("John");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getCreatedAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void usernameIsEmailAndPasswordIsExposed() {
        UserDetailsImpl user = user();

        assertThat(user.getUsername()).isEqualTo("john@example.com");
        assertThat(user.getPassword()).isEqualTo("hash");
    }

    @Test
    void hasNoAuthorities() {
        assertThat(user().getAuthorities()).isEmpty();
    }

    @Test
    void accountFlagsAreAllTrue() {
        UserDetailsImpl user = user();

        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }
}
