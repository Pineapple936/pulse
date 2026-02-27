package com.pulse.entity.dto;

public record ProgressEditDto(
        int repetitions,
        int sets,
        float weight
) {
}
