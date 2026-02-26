package com.pulse.controller;

import com.pulse.entity.dto.auth.JWTAuthentificationDto;
import com.pulse.entity.dto.auth.LoginDto;
import com.pulse.entity.dto.auth.RefreshTokenDto;
import com.pulse.entity.dto.auth.RegisterDto;
import com.pulse.entity.dto.response.ResponseMessageDto;
import com.pulse.service.UserService;
import com.pulse.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseMessageDto> register(@Valid @RequestBody RegisterDto dto) {
        userService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.registeredMessage());
    }

    @PostMapping("/login")
    public ResponseEntity<JWTAuthentificationDto> login(@Valid @RequestBody LoginDto dto) throws AuthenticationException {
        JWTAuthentificationDto jwtAuthenticationDto = userService.singIn(dto);
        return ResponseEntity.ok(jwtAuthenticationDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JWTAuthentificationDto> refreshToken(@Valid @RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        return ResponseEntity.ok(userService.refreshToken(refreshTokenDto));
    }
}
