package com.pulse.service.security;

import com.pulse.controller.auth.entity.response.AuthTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtCoreTest {

    private static final String SECRET = "211e4129e577d9ea9dd8121745a497c6e62d4416f8186cfe7179cf29b4cb2fe3";
    private static final String EMAIL = "john@example.com";

    private JwtCore jwtCore;

    @BeforeEach
    void setUp() {
        jwtCore = new JwtCore(SECRET, 15, 30);
    }

    @Test
    void createAuthToken_producesAccessAndRefreshTokens() {
        AuthTokenResponse response = jwtCore.createAuthToken(EMAIL);

        assertThat(response.token()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
    }

    @Test
    void generatedToken_isValidAndCarriesEmail() {
        AuthTokenResponse response = jwtCore.createAuthToken(EMAIL);

        assertThat(jwtCore.validateJwtToken(response.token())).isTrue();
        assertThat(jwtCore.getEmailFromToken(response.token())).isEqualTo(EMAIL);
        assertThat(jwtCore.getEmailFromToken(response.refreshToken())).isEqualTo(EMAIL);
    }

    @Test
    void createAuthToken_withExistingRefreshToken_keepsThatRefreshToken() {
        String existingRefresh = jwtCore.createAuthToken(EMAIL).refreshToken();

        AuthTokenResponse response = jwtCore.createAuthToken(EMAIL, existingRefresh);

        assertThat(response.refreshToken()).isEqualTo(existingRefresh);
        assertThat(jwtCore.validateJwtToken(response.token())).isTrue();
    }

    @Test
    void validateJwtToken_returnsFalseForGarbage() {
        assertThat(jwtCore.validateJwtToken("not-a-jwt")).isFalse();
    }

    @Test
    void validateJwtToken_returnsFalseForTokenSignedWithAnotherKey() {
        JwtCore otherCore = new JwtCore(
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 15, 30);
        String foreignToken = otherCore.createAuthToken(EMAIL).token();

        assertThat(jwtCore.validateJwtToken(foreignToken)).isFalse();
    }
}
