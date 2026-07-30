package com.pulse.controller.workout.entity;

import com.pulse.controller.workout.entity.response.WorkoutResponse;
import com.pulse.jooq.tables.records.WorkoutRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {
    WorkoutResponse toResponse(WorkoutRecord workoutRecord);
}
