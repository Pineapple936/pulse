package com.pulse.entity.dto;

public record ProgressDetailsDto(
        Long exerciseId,
        int repetitions,
        int sets,
        float weight
) {
}
