package com.pulse.controller;

import com.pulse.entity.dto.auth.JWTAuthentificationDto;
import com.pulse.entity.dto.auth.LoginDto;
import com.pulse.entity.dto.auth.RefreshTokenDto;
import com.pulse.entity.dto.auth.RegisterDto;
import com.pulse.entity.dto.response.ResponseMessageDto;
import com.pulse.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.naming.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_shouldReturnCreated() {
        RegisterDto dto = new RegisterDto("john", "john@example.com", "pass");
        ResponseEntity<ResponseMessageDto> resp = controller.register(dto);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        verify(userService, times(1)).save(dto);
        assertNotNull(resp.getBody());
    }

    @Test
    void login_shouldReturnToken() throws AuthenticationException {
        LoginDto dto = new LoginDto("john@example.com", "pass");
        JWTAuthentificationDto token = new JWTAuthentificationDto("access", "refresh");
        when(userService.signIn(dto)).thenReturn(token);

        ResponseEntity<JWTAuthentificationDto> resp = controller.login(dto);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(token, resp.getBody());
    }

    @Test
    void login_shouldPropagateException() throws AuthenticationException {
        LoginDto dto = new LoginDto("john@example.com", "pass");
        when(userService.signIn(dto)).thenThrow(new AuthenticationException("bad"));
        assertThrows(AuthenticationException.class, () -> controller.login(dto));
    }

    @Test
    void refreshToken_shouldReturnNewToken() throws Exception {
        RefreshTokenDto dto = new RefreshTokenDto("refresh");
        JWTAuthentificationDto token = new JWTAuthentificationDto("acc", "ref");
        when(userService.refreshToken(dto)).thenReturn(token);

        ResponseEntity<JWTAuthentificationDto> resp = controller.refreshToken(dto);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(token, resp.getBody());
    }

    @Test
    void refreshToken_shouldPropagateException() throws Exception {
        RefreshTokenDto dto = new RefreshTokenDto("refresh");
        when(userService.refreshToken(dto)).thenThrow(new javax.naming.AuthenticationException("oops"));
        assertThrows(javax.naming.AuthenticationException.class, () -> controller.refreshToken(dto));
    }
}
