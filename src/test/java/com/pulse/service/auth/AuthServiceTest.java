package com.pulse.service.auth;

import com.pulse.controller.auth.entity.request.RegisterUserRequest;
import com.pulse.controller.auth.entity.response.AuthTokenResponse;
import com.pulse.jooq.tables.records.UserRecord;
import com.pulse.service.security.JwtCore;
import com.pulse.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String EMAIL = "john@example.com";
    private static final String PASSWORD = "password1";
    private static final String REFRESH_TOKEN = "refresh-token";

    @Mock
    private JwtCore jwtCore;
    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService service;

    private UserRecord user() {
        UserRecord user = new UserRecord();
        user.setId(1L);
        user.setName("John");
        user.setEmail(EMAIL);
        user.setPassword("hash");
        return user;
    }

    @Test
    void register_savesUserAndReturnsToken() {
        RegisterUserRequest req = new RegisterUserRequest("John", null, null, null, null, EMAIL, PASSWORD);
        AuthTokenResponse expected = new AuthTokenResponse("access", "refresh");
        when(userService.save(req)).thenReturn(user());
        when(jwtCore.createAuthToken(EMAIL)).thenReturn(expected);

        assertThat(service.register(req)).isSameAs(expected);
    }

    @Test
    void login_returnsTokenWhenCredentialsValid() throws AuthenticationException {
        AuthTokenResponse expected = new AuthTokenResponse("access", "refresh");
        when(userService.findByCredentials(EMAIL, PASSWORD)).thenReturn(user());
        when(jwtCore.createAuthToken(EMAIL)).thenReturn(expected);

        assertThat(service.login(EMAIL, PASSWORD)).isSameAs(expected);
    }

    @Test
    void login_propagatesAuthenticationExceptionOnWrongPassword() throws AuthenticationException {
        when(userService.findByCredentials(EMAIL, PASSWORD))
                .thenThrow(new AuthenticationException("Password is not correct"));

        assertThatThrownBy(() -> service.login(EMAIL, PASSWORD))
                .isInstanceOf(AuthenticationException.class);
        verify(jwtCore, never()).createAuthToken(anyString());
    }

    @Test
    void login_propagatesNoSuchElementWhenEmailUnknown() throws AuthenticationException {
        when(userService.findByCredentials(EMAIL, PASSWORD))
                .thenThrow(new NoSuchElementException("User with email " + EMAIL + " not found"));

        assertThatThrownBy(() -> service.login(EMAIL, PASSWORD))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void refreshToken_returnsNewTokenWhenValid() throws AuthenticationException {
        AuthTokenResponse expected = new AuthTokenResponse("new-access", REFRESH_TOKEN);
        when(jwtCore.validateJwtToken(REFRESH_TOKEN)).thenReturn(true);
        when(jwtCore.getEmailFromToken(REFRESH_TOKEN)).thenReturn(EMAIL);
        when(userService.findByEmail(EMAIL)).thenReturn(user());
        when(jwtCore.createAuthToken(EMAIL, REFRESH_TOKEN)).thenReturn(expected);

        assertThat(service.refreshToken(REFRESH_TOKEN)).isSameAs(expected);
    }

    @Test
    void refreshToken_throwsWhenTokenNull() {
        assertThatThrownBy(() -> service.refreshToken(null))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    void refreshToken_throwsWhenTokenInvalid() {
        when(jwtCore.validateJwtToken(REFRESH_TOKEN)).thenReturn(false);

        assertThatThrownBy(() -> service.refreshToken(REFRESH_TOKEN))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    void refreshToken_throwsWhenUserForTokenMissing() {
        when(jwtCore.validateJwtToken(REFRESH_TOKEN)).thenReturn(true);
        when(jwtCore.getEmailFromToken(REFRESH_TOKEN)).thenReturn(EMAIL);
        when(userService.findByEmail(EMAIL)).thenThrow(new NoSuchElementException("not found"));

        assertThatThrownBy(() -> service.refreshToken(REFRESH_TOKEN))
                .isInstanceOf(NoSuchElementException.class);
    }
}
