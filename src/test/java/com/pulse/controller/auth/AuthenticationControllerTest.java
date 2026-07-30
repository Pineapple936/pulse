package com.pulse.controller.auth;

import com.pulse.controller.error.GlobalExceptionHandler;
import com.pulse.controller.auth.entity.response.AuthTokenResponse;
import com.pulse.repository.user.entity.UserDetailsImpl;
import com.pulse.service.auth.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthService service;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AuthenticationController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        UserDetailsImpl principal = new UserDetailsImpl(1L, "John", "john@example.com", "hash", null);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void register_returns201WithToken() throws Exception {
        when(service.register(any())).thenReturn(new AuthTokenResponse("access", "refresh"));
        String body = """
                {"name":"John","email":"john@example.com","password":"password1"}""";

        mvc.perform(post("/api/auth/register").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));
    }

    @Test
    void register_returns400WhenBodyInvalid() throws Exception {
        String body = """
                {"name":"","email":"not-an-email","password":"short"}""";

        mvc.perform(post("/api/auth/register").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_returns200() throws Exception {
        when(service.login("john@example.com", "password1"))
                .thenReturn(new AuthTokenResponse("access", "refresh"));
        String body = """
                {"email":"john@example.com","password":"password1"}""";

        mvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access"));
    }

    @Test
    void login_returns400WhenBodyInvalid() throws Exception {
        String body = """
                {"email":"not-an-email","password":"short"}""";

        mvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_returns401WhenPasswordWrong() throws Exception {
        when(service.login(anyString(), anyString()))
                .thenThrow(new AuthenticationException("Password is not correct"));
        String body = """
                {"email":"john@example.com","password":"password1"}""";

        mvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_returns404WhenEmailUnknown() throws Exception {
        when(service.login(anyString(), anyString()))
                .thenThrow(new NoSuchElementException("User with email john@example.com not found"));
        String body = """
                {"email":"john@example.com","password":"password1"}""";

        mvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void refresh_returns200() throws Exception {
        when(service.refreshToken("refresh")).thenReturn(new AuthTokenResponse("access", "refresh"));
        String body = """
                {"refreshToken":"refresh"}""";

        mvc.perform(post("/api/auth/refresh").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access"));
    }

    @Test
    void refresh_returns400WhenTokenBlank() throws Exception {
        String body = """
                {"refreshToken":""}""";

        mvc.perform(post("/api/auth/refresh").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refresh_returns401WhenTokenInvalid() throws Exception {
        when(service.refreshToken(anyString()))
                .thenThrow(new AuthenticationException("Invalid refresh token"));
        String body = """
                {"refreshToken":"bad-token"}""";

        mvc.perform(post("/api/auth/refresh").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
    }
}
