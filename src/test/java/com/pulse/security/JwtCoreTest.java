package com.pulse.security;

import com.pulse.entity.dto.auth.JWTAuthentificationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtCoreTest {
    private JwtCore jwtCore;

    @BeforeEach
    void setUp() {
        String secret = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
        jwtCore = new JwtCore(secret, 60, 1);
    }

    @Test
    void createAuthToken_and_validate() {
        JWTAuthentificationDto dto = jwtCore.createAuthToken("foo@bar");
        assertTrue(jwtCore.validateJwtToken(dto.token()));
        assertEquals("foo@bar", jwtCore.getEmailFromToken(dto.token()));
        assertTrue(jwtCore.validateJwtToken(dto.refreshToken()));
        JWTAuthentificationDto dto2 = jwtCore.createAuthToken("foo@bar", dto.refreshToken());
        assertEquals(dto.refreshToken(), dto2.refreshToken());
    }

    @Test
    void validateJwtToken_invalid() {
        assertFalse(jwtCore.validateJwtToken("not-a-token"));
    }
}
