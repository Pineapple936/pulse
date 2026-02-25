package com.example.demo.entity.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDto(
        @NotBlank(message = "Email cannot be blank or empty")
        @Email(message = "Please provide a valid email address")
        @Size(max = 100, message = "Email cannot be longer than 100 characters")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
        String password
) {
}
