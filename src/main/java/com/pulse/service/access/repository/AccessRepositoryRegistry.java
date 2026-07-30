package com.pulse.service.access.repository;

import com.pulse.repository.interfaces.AccessRepository;
import com.pulse.service.access.repository.entity.AccessResourceType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AccessRepositoryRegistry {
    private final Map<AccessResourceType, AccessRepository> handlersByType;

    public AccessRepositoryRegistry(List<AccessRepository> handlers) {
        handlersByType = handlers.stream()
                .collect(Collectors.toMap(AccessRepository::getType, Function.identity()));
    }

    public void checkAccess(AccessResourceType type, Long id, Long userId) {
        if (!getRepositoryByType(type).hasAccess(id, userId)) {
            throw new AccessDeniedException(
                    "%s with id=%d does not belong to userId=%d".formatted(type, id, userId));
        }
    }

    private AccessRepository getRepositoryByType(AccessResourceType type) {
        return handlersByType.get(type);
    }

}
