package com.example.demo.entity.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @NotBlank(message = "Name cannot be empty or blank")
        @Size(max = 50, message = "Name cannot be longer than 50 characters")
        String name,

        @Email(message = "Invalid email address")
        @Size(max = 100, message = "Email cannot be longer than 100 characters")
        @NotBlank(message = "Email cannot be empty")
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
        String password
) {
}

