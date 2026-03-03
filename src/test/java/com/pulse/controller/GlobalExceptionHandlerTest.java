package com.pulse.controller;

import com.pulse.entity.dto.response.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.naming.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void authentificationExceptionHandler_shouldReturnUnauthorized() throws Exception {
        AuthenticationException exception = new AuthenticationException("bad credentials");

        ResponseEntity<ErrorResponseDto> response = handler.authentificationExceptionHandler(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unauthorized", response.getBody().name());
        assertEquals("bad credentials", response.getBody().message());
        assertNotNull(response.getBody().localDateTime());
    }

    @Test
    void notFoundExceptionHandler_shouldReturnNotFound() {
        EntityNotFoundException exception = new EntityNotFoundException("workout not found");

        ResponseEntity<ErrorResponseDto> response = handler.notFoundExceptionHandler(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Entity not found", response.getBody().name());
        assertEquals("workout not found", response.getBody().message());
        assertNotNull(response.getBody().localDateTime());
    }

    @Test
    void validationExceptionHandler_shouldAggregateFieldErrors() {
        BindingResult bindingResult = new BindException(new Object(), "dto");
        bindingResult.addError(new FieldError("dto", "email", "must not be blank"));
        bindingResult.addError(new FieldError("dto", "password", "size must be between 8 and 64"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                mock(MethodParameter.class),
                bindingResult
        );

        ResponseEntity<ErrorResponseDto> response = handler.validationExceptionHandler(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().name());
        assertEquals(
                "email: must not be blank, password: size must be between 8 and 64",
                response.getBody().message()
        );
        assertNotNull(response.getBody().localDateTime());
    }

    @Test
    void argumentExceptionHandler_shouldReturnBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("invalid id");

        ResponseEntity<ErrorResponseDto> response = handler.argumentExceptionHandler(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad request", response.getBody().name());
        assertEquals("invalid id", response.getBody().message());
        assertNotNull(response.getBody().localDateTime());
    }

    @Test
    void argumentExceptionHandler_shouldHandleHttpMessageNotReadable() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
                "malformed json",
                new MockHttpInputMessage(new byte[0])
        );

        ResponseEntity<ErrorResponseDto> response = handler.argumentExceptionHandler(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad request", response.getBody().name());
        assertEquals("malformed json", response.getBody().message());
        assertNotNull(response.getBody().localDateTime());
    }

    @Test
    void exceptionHandler_shouldReturnInternalServerError() {
        RuntimeException exception = new RuntimeException("unexpected error");

        ResponseEntity<ErrorResponseDto> response = handler.exceptionHandler(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().name());
        assertEquals("unexpected error", response.getBody().message());
        assertNotNull(response.getBody().localDateTime());
    }
}
