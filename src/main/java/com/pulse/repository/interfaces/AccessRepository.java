package com.pulse.repository.interfaces;

import com.pulse.service.access.repository.entity.AccessResourceType;

public interface AccessRepository {
    boolean hasAccess(Long id, Long userId);
    AccessResourceType getType();
}
