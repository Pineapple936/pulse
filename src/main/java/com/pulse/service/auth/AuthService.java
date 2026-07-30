package com.pulse.service.auth;

import com.pulse.controller.auth.entity.request.RegisterUserRequest;
import com.pulse.controller.auth.entity.response.AuthTokenResponse;
import com.pulse.jooq.tables.records.UserRecord;
import com.pulse.service.security.JwtCore;
import com.pulse.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final JwtCore jwtCore;
    private final UserService userService;

    public AuthTokenResponse register(RegisterUserRequest req) {
        UserRecord record = userService.save(req);
        var token = jwtCore.createAuthToken(record.getEmail());
        log.info("User with email={} registered successfully", record.getEmail());
        return token;
    }

    public AuthTokenResponse login(String email, String password) throws AuthenticationException {
        UserRecord user = userService.findByCredentials(email, password);

        log.info("Sign-in successful for email={}", user.getEmail());
        return jwtCore.createAuthToken(user.getEmail());
    }

    public AuthTokenResponse refreshToken(String refreshToken) throws AuthenticationException {
        if (refreshToken != null && jwtCore.validateJwtToken(refreshToken)) {
            UserRecord user = userService.findByEmail(jwtCore.getEmailFromToken(refreshToken));
            log.info("Refresh token accepted for email={}", user.getEmail());
            return jwtCore.createAuthToken(user.getEmail(), refreshToken);
        }
        log.warn("Refresh token rejected: invalid or expired");
        throw new AuthenticationException("Invalid refresh token");
    }
}
