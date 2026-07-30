package com.pulse.controller.progress.entity.response;

import java.math.BigDecimal;

public record ProgressResponse(
        Long id,
        Long exerciseId,
        Integer setNumber,
        Integer repetition,
        BigDecimal weight
) {
}
