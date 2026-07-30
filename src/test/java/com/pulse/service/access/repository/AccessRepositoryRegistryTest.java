package com.pulse.service.access.repository;

import com.pulse.repository.interfaces.AccessRepository;
import com.pulse.service.access.repository.entity.AccessResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccessRepositoryRegistryTest {

    private static final Long RESOURCE_ID = 5L;
    private static final Long USER_ID = 1L;

    private AccessRepository workoutHandler;
    private AccessRepositoryRegistry registry;

    @BeforeEach
    void setUp() {
        workoutHandler = mock(AccessRepository.class);
        when(workoutHandler.getType()).thenReturn(AccessResourceType.WORKOUT);
        registry = new AccessRepositoryRegistry(List.of(workoutHandler));
    }

    @Test
    void checkAccess_passesWhenHandlerGrantsAccess() {
        when(workoutHandler.hasAccess(RESOURCE_ID, USER_ID)).thenReturn(true);

        assertThatCode(() -> registry.checkAccess(AccessResourceType.WORKOUT, RESOURCE_ID, USER_ID))
                .doesNotThrowAnyException();
    }

    @Test
    void checkAccess_throwsWhenHandlerDeniesAccess() {
        when(workoutHandler.hasAccess(RESOURCE_ID, USER_ID)).thenReturn(false);

        assertThatThrownBy(() -> registry.checkAccess(AccessResourceType.WORKOUT, RESOURCE_ID, USER_ID))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("does not belong");
    }
}
