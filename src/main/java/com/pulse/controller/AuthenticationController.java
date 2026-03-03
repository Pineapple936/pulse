package com.pulse.controller;

import com.pulse.entity.User;
import com.pulse.entity.dto.auth.JWTAuthentificationDto;
import com.pulse.entity.dto.auth.LoginDto;
import com.pulse.entity.dto.auth.RefreshTokenDto;
import com.pulse.entity.dto.auth.RegisterDto;
import com.pulse.entity.dto.response.ResponseMessageDto;
import com.pulse.service.UserService;
import com.pulse.util.ResponseUtil;
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
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseMessageDto> register(@Valid @RequestBody RegisterDto dto) {
        log.info("Auth API register request for email={}", dto.email());
        userService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.registeredMessage());
    }

    @PostMapping("/login")
    public ResponseEntity<JWTAuthentificationDto> login(@Valid @RequestBody LoginDto dto) throws AuthenticationException {
        log.info("Auth API login request for email={}", dto.email());
        JWTAuthentificationDto jwtAuthenticationDto = userService.signIn(dto);
        return ResponseEntity.ok(jwtAuthenticationDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JWTAuthentificationDto> refreshToken(@Valid @RequestBody RefreshTokenDto refreshTokenDto,
                                                               @AuthenticationPrincipal User user) throws Exception {
        log.info("Auth API refresh-token request for email={}", user.getEmail());
        return ResponseEntity.ok(userService.refreshToken(refreshTokenDto));
    }
}
