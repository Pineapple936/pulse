package com.pulse.service.user;

import com.pulse.controller.auth.entity.request.RegisterUserRequest;
import com.pulse.jooq.tables.records.UserRecord;
import com.pulse.repository.user.UserRepository;
import com.pulse.service.user.entity.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String NAME = "John Doe";
    private static final String EMAIL = "john@example.com";
    private static final String RAW_PASSWORD = "plainPassword";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final Integer AGE = 30;
    private static final BigDecimal WEIGHT = new BigDecimal("80.50");
    private static final BigDecimal HEIGHT = new BigDecimal("180.00");

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository repo;

    @InjectMocks
    private UserService service;

    private UserRecord userRecord() {
        UserRecord user = new UserRecord();
        user.setId(1L);
        user.setName(NAME);
        user.setEmail(EMAIL);
        user.setPassword(ENCODED_PASSWORD);
        return user;
    }

    private RegisterUserRequest request(Gender gender) {
        return new RegisterUserRequest(NAME, gender, AGE, WEIGHT, HEIGHT, EMAIL, RAW_PASSWORD);
    }

    @Test
    void save_encodesPasswordAndPersistsAllFields() {
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(repo.save(any(UserRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserRecord result = service.save(request(Gender.MALE));

        ArgumentCaptor<UserRecord> captor = ArgumentCaptor.forClass(UserRecord.class);
        verify(repo).save(captor.capture());
        UserRecord persisted = captor.getValue();
        assertThat(persisted.getName()).isEqualTo(NAME);
        assertThat(persisted.getEmail()).isEqualTo(EMAIL);
        assertThat(persisted.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(persisted.getGender()).isEqualTo("MALE");
        assertThat(persisted.getAge()).isEqualTo(AGE);
        assertThat(persisted.getWeight()).isEqualTo(WEIGHT);
        assertThat(persisted.getHeight()).isEqualTo(HEIGHT);
        assertThat(result).isSameAs(persisted);
    }

    @Test
    void save_leavesGenderNullWhenNotProvided() {
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(repo.save(any(UserRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.save(request(null));

        ArgumentCaptor<UserRecord> captor = ArgumentCaptor.forClass(UserRecord.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getGender()).isNull();
    }

    @Test
    void findByEmail_returnsUserWhenFound() {
        UserRecord user = userRecord();
        when(repo.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThat(service.findByEmail(EMAIL)).isSameAs(user);
    }

    @Test
    void findByEmail_throwsWhenMissing() {
        when(repo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByEmail(EMAIL))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(EMAIL);
    }

    @Test
    void findByCredentials_returnsUserWhenPasswordMatches() throws AuthenticationException {
        UserRecord user = userRecord();
        when(repo.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        assertThat(service.findByCredentials(EMAIL, RAW_PASSWORD)).isSameAs(user);
    }

    @Test
    void findByCredentials_throwsAuthenticationExceptionWhenPasswordWrong() {
        UserRecord user = userRecord();
        when(repo.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThatThrownBy(() -> service.findByCredentials(EMAIL, RAW_PASSWORD))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void findByCredentials_throwsNoSuchElementWhenEmailUnknown() {
        when(repo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByCredentials(EMAIL, RAW_PASSWORD))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void deleteById_delegatesToRepository() {
        service.deleteById(1L);

        verify(repo).deleteById(1L);
    }
}
