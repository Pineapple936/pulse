package com.pulse.service.progress;

import com.pulse.controller.progress.entity.request.CreateProgressRequest;
import com.pulse.controller.progress.entity.request.EditProgressRequest;
import com.pulse.jooq.tables.records.ProgressRecord;
import com.pulse.repository.interfaces.FindableByParent;
import com.pulse.service.access.repository.AccessRepositoryRegistry;
import com.pulse.service.access.repository.entity.AccessResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long EXERCISE_ID = 20L;
    private static final Long PROGRESS_ID = 40L;

    @Mock
    private FindableByParent<ProgressRecord> repo;
    @Mock
    private AccessRepositoryRegistry accessRegistry;

    @InjectMocks
    private ProgressService service;

    @Test
    void save_persistsWhenAccessGranted() {
        CreateProgressRequest req = new CreateProgressRequest(EXERCISE_ID, 1, 10, new BigDecimal("50.00"));
        ProgressRecord saved = new ProgressRecord(PROGRESS_ID, 1, 10, new BigDecimal("50.00"), EXERCISE_ID, null);
        when(repo.save(any(ProgressRecord.class))).thenReturn(saved);

        ProgressRecord result = service.save(req, USER_ID);

        assertThat(result).isSameAs(saved);
        verify(accessRegistry).checkAccess(AccessResourceType.EXERCISE, EXERCISE_ID, USER_ID);
    }

    @Test
    void save_throwsAccessDeniedWhenExerciseNotOwned() {
        CreateProgressRequest req = new CreateProgressRequest(EXERCISE_ID, 1, 10, new BigDecimal("50.00"));
        doThrow(new AccessDeniedException("denied"))
                .when(accessRegistry).checkAccess(AccessResourceType.EXERCISE, EXERCISE_ID, USER_ID);

        assertThatThrownBy(() -> service.save(req, USER_ID))
                .isInstanceOf(AccessDeniedException.class);

        verify(repo, never()).save(any());
    }

    @Test
    void findById_returnsRecordWhenFound() {
        ProgressRecord record = new ProgressRecord(PROGRESS_ID, 1, 10, new BigDecimal("50.00"), EXERCISE_ID, null);
        when(repo.findById(PROGRESS_ID)).thenReturn(Optional.of(record));

        ProgressRecord result = service.findById(PROGRESS_ID, USER_ID);

        assertThat(result).isSameAs(record);
        verify(accessRegistry).checkAccess(AccessResourceType.PROGRESS, PROGRESS_ID, USER_ID);
    }

    @Test
    void findById_throwsNotFoundWhenMissing() {
        when(repo.findById(PROGRESS_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(PROGRESS_ID, USER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void findAllByExerciseId_delegatesToRepository() {
        List<ProgressRecord> expected = List.of(new ProgressRecord());
        when(repo.findByParent(eq(EXERCISE_ID), any())).thenReturn(expected);

        List<ProgressRecord> result = service.findAllByExerciseId(EXERCISE_ID, USER_ID, 0, 20);

        assertThat(result).isSameAs(expected);
        verify(accessRegistry).checkAccess(AccessResourceType.EXERCISE, EXERCISE_ID, USER_ID);
    }

    @Test
    void update_appliesNonNullFieldsAndStores() {
        ProgressRecord record = mock(ProgressRecord.class);
        when(repo.findById(PROGRESS_ID)).thenReturn(Optional.of(record));
        EditProgressRequest req = new EditProgressRequest(PROGRESS_ID, 12, 3, new BigDecimal("60.00"));

        service.update(req, USER_ID);

        verify(record).setRepetition(12);
        verify(record).setSetNumber(3);
        verify(record).setWeight(new BigDecimal("60.00"));
        verify(record).store();
    }

    @Test
    void update_clearsWeightButKeepsNotNullFieldsWhenNull() {
        ProgressRecord record = mock(ProgressRecord.class);
        when(repo.findById(PROGRESS_ID)).thenReturn(Optional.of(record));
        EditProgressRequest req = new EditProgressRequest(PROGRESS_ID, null, null, null);

        service.update(req, USER_ID);

        verify(record, never()).setRepetition(any());
        verify(record, never()).setSetNumber(any());
        verify(record).setWeight(null);
        verify(record).store();
    }

    @Test
    void deleteById_checksAccessThenDeletes() {
        service.deleteById(PROGRESS_ID, USER_ID);

        verify(accessRegistry).checkAccess(AccessResourceType.PROGRESS, PROGRESS_ID, USER_ID);
        verify(repo).deleteById(PROGRESS_ID);
    }
}
