package com.pulse.service;

import com.pulse.entity.User;
import com.pulse.entity.dto.auth.JWTAuthentificationDto;
import com.pulse.entity.dto.auth.LoginDto;
import com.pulse.entity.dto.auth.RefreshTokenDto;
import com.pulse.entity.dto.auth.RegisterDto;
import com.pulse.repository.UserRepository;
import com.pulse.security.JwtCore;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private JwtCore jwtCore;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(mock(SecurityContext.class));
    }

    @Test
    void save_shouldPersistAndSetSecurityContext() {
        RegisterDto dto = new RegisterDto("n", "e", "p");
        User user = new User(dto.name(), dto.email(), "encoded");
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded");
        when(repository.save(any(User.class))).thenReturn(user);

        service.save(dto);
        verify(repository).save(argThat(saved ->
                dto.name().equals(saved.getName())
                        && dto.email().equals(saved.getEmail())
                        && "encoded".equals(saved.getPassword())
        ));
        verify(SecurityContextHolder.getContext()).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void signIn_success() throws AuthenticationException {
        LoginDto dto = new LoginDto("e", "p");
        User user = new User();
        user.setEmail("e");
        when(repository.findByEmail(dto.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.password(), user.getPassword())).thenReturn(true);
        JWTAuthentificationDto token = new JWTAuthentificationDto("a","r");
        when(jwtCore.createAuthToken(user.getEmail())).thenReturn(token);

        JWTAuthentificationDto result = service.signIn(dto);
        assertSame(token, result);
    }

    @Test
    void signIn_badCredentials() {
        LoginDto dto = new LoginDto("e", "p");
        when(repository.findByEmail(dto.email())).thenReturn(Optional.empty());
        assertThrows(AuthenticationException.class, () -> service.signIn(dto));
        verify(jwtCore, never()).createAuthToken(anyString());
    }

    @Test
    void signIn_passwordMismatch() {
        LoginDto dto = new LoginDto("e", "bad");
        User user = new User();
        user.setEmail("e");
        user.setPassword("encoded");
        when(repository.findByEmail(dto.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.password(), user.getPassword())).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> service.signIn(dto));
        verify(jwtCore, never()).createAuthToken(anyString());
    }

    @Test
    void refreshToken_valid() throws AuthenticationException {
        RefreshTokenDto dto = new RefreshTokenDto("rt");
        when(jwtCore.validateJwtToken(dto.refreshToken())).thenReturn(true);
        when(jwtCore.getEmailFromToken(dto.refreshToken())).thenReturn("e");
        User user = new User();
        user.setEmail("e");
        when(repository.findByEmail("e")).thenReturn(Optional.of(user));
        JWTAuthentificationDto token = new JWTAuthentificationDto("a","r");
        when(jwtCore.createAuthToken(user.getEmail(), dto.refreshToken())).thenReturn(token);

        assertSame(token, service.refreshToken(dto));
    }

    @Test
    void refreshToken_invalid() {
        RefreshTokenDto dto = new RefreshTokenDto("rt");
        when(jwtCore.validateJwtToken(dto.refreshToken())).thenReturn(false);
        assertThrows(AuthenticationException.class, () -> service.refreshToken(dto));
    }

    @Test
    void refreshToken_nullToken() {
        RefreshTokenDto dto = new RefreshTokenDto(null);
        assertThrows(AuthenticationException.class, () -> service.refreshToken(dto));
        verify(jwtCore, never()).validateJwtToken(anyString());
    }

    @Test
    void getCurrentUser_shouldQueryContext() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("e","x",new HashSet<>());
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(auth);
        User user = new User();
        when(repository.findByEmail("e")).thenReturn(Optional.of(user));
        User actual = service.getCurrentUser();
        assertSame(user, actual);
    }

    @Test
    void getCurrentUser_notFound() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("missing@mail","x",new HashSet<>());
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(auth);
        when(repository.findByEmail("missing@mail")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getCurrentUser());
    }
}
