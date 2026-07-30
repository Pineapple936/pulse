package com.pulse.controller.auth;

import com.pulse.controller.auth.entity.request.RegisterUserRequest;
import com.pulse.repository.user.entity.UserDetailsImpl;
import com.pulse.controller.auth.entity.response.AuthTokenResponse;
import com.pulse.controller.auth.entity.request.LoginRequest;
import com.pulse.controller.auth.entity.request.RefreshTokenRequest;
import com.pulse.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<AuthTokenResponse> register(@Valid @RequestBody RegisterUserRequest req) {
        log.info("Auth API register request for email={}", req.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody LoginRequest req) throws AuthenticationException {
        log.info("Auth API login request for email={}", req.email());
        return ResponseEntity.ok(service.login(req.email(), req.password()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest req,
                                                          @AuthenticationPrincipal UserDetailsImpl user) throws AuthenticationException {
        log.info("Auth API refresh-token request for email={}", user.getEmail());
        return ResponseEntity.ok(service.refreshToken(req.refreshToken()));
    }
}
