package com.example.demo.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ExerciseDto(
        @NotBlank(message = "Exercise name cannot be blank or empty")
        @Size(max = 30, message = "Exercise name cannot be longer than 30 characters")
        String name
) {
}
