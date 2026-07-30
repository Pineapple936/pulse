package com.pulse.controller.exercise.entity;

import com.pulse.controller.exercise.entity.response.ExerciseResponse;
import com.pulse.jooq.tables.records.ExerciseRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    ExerciseResponse toResponse(ExerciseRecord exerciseRecord);
}
