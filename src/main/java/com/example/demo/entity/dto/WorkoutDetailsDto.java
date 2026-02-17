package com.example.demo.entity.dto;

import java.time.LocalDate;

public record WorkoutDetailsDto(
        String name,
        LocalDate date
) {
}
