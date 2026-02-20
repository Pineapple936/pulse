package com.example.demo.controller;

import com.example.demo.entity.dto.*;
import com.example.demo.service.UserService;
import com.example.demo.util.ResponseUtil;
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
    public ResponseEntity<ResponseMessageDto> register(@RequestBody RegisterDto dto) {
        userService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.registeredMessage());
    }

    @PostMapping("/login")
    public ResponseEntity<JWTAuthentificationDto> login(@RequestBody LoginDto dto) {
        try {
            JWTAuthentificationDto jwtAuthenticationDto = userService.singIn(dto);
            return ResponseEntity.ok(jwtAuthenticationDto);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JWTAuthentificationDto> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        return ResponseEntity.ok(userService.refreshToken(refreshTokenDto));
    }
}
