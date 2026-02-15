package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.dto.RegisterDto;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
@AllArgsConstructor
public class AuthenticationController {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) {
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword(), new HashSet<>()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body("User with email " + user.getEmail() + " created");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RegisterDto dto) {
        User user = userRepository.findByEmail(dto.email()).orElseThrow(
                () -> new UsernameNotFoundException("User with email " + dto.email() + " not found")
        );

        if(!passwordEncoder.matches(dto.password(), user.getPassword()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword(), new HashSet<>()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);



        return ResponseEntity.status(HttpStatus.OK).body("Login successful");
    }

}
