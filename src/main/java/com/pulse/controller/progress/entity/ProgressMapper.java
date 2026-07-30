package com.pulse.controller.progress.entity;

import com.pulse.controller.progress.entity.response.ProgressResponse;
import com.pulse.jooq.tables.records.ProgressRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgressMapper {
    ProgressResponse toResponse(ProgressRecord progressRecord);
}
