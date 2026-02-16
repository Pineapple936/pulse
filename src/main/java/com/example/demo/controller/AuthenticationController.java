package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.dto.JWTAuthentificationDto;
import com.example.demo.entity.dto.LoginDto;
import com.example.demo.entity.dto.RefreshTokenDto;
import com.example.demo.entity.dto.RegisterDto;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthenticationController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<JWTAuthentificationDto> login(@RequestBody LoginDto dto) {
        try {
            JWTAuthentificationDto jwtAuthenticationDto = userService.singIn(dto);
            return ResponseEntity.ok(jwtAuthenticationDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JWTAuthentificationDto> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        return ResponseEntity.ok(userService.refreshToken(refreshTokenDto));
    }
}
