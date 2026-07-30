package com.pulse.controller.auth.entity.request;

import com.pulse.service.user.entity.Gender;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RegisterUserRequest(
        @NotBlank(message = "Name cannot be empty or blank")
        @Size(max = 50, message = "Name cannot be longer than 50 characters")
        String name,

        Gender gender,

        @Positive(message = "Age must be a positive number")
        @Max(value = 150, message = "Age must be realistic")
        Integer age,

        @Positive(message = "Weight must be a positive number")
        @Digits(integer = 3, fraction = 2, message = "Weight must have at most 3 integer and 2 fraction digits")
        BigDecimal weight,

        @Positive(message = "Height must be a positive number")
        @Digits(integer = 3, fraction = 2, message = "Height must have at most 3 integer and 2 fraction digits")
        BigDecimal height,

        @Email(message = "Invalid email address")
        @Size(max = 100, message = "Email cannot be longer than 100 characters")
        @NotBlank(message = "Email cannot be empty")
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
        String password
) {
}
