package com.pulse.service.workout;

import com.pulse.controller.workout.entity.request.CreateWorkoutRequest;
import com.pulse.controller.workout.entity.request.EditWorkoutRequest;
import com.pulse.jooq.tables.records.WorkoutRecord;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long WORKOUT_ID = 10L;

    @Mock
    private FindableByParent<WorkoutRecord> repo;
    @Mock
    private AccessRepositoryRegistry accessRegistry;

    @InjectMocks
    private WorkoutService service;

    @Test
    void save_setsFieldsAndReturnsSavedRecord() {
        OffsetDateTime performedAt = OffsetDateTime.now();
        CreateWorkoutRequest req = new CreateWorkoutRequest("Leg day", performedAt);
        WorkoutRecord saved = new WorkoutRecord(WORKOUT_ID, "Leg day", performedAt, USER_ID, null);
        when(repo.save(any(WorkoutRecord.class))).thenReturn(saved);

        WorkoutRecord result = service.save(req, USER_ID);

        assertThat(result).isSameAs(saved);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        verify(repo).save(any(WorkoutRecord.class));
    }

    @Test
    void findById_returnsRecordWhenAccessGrantedAndFound() {
        WorkoutRecord record = new WorkoutRecord(WORKOUT_ID, "Leg day", OffsetDateTime.now(), USER_ID, null);
        when(repo.findById(WORKOUT_ID)).thenReturn(Optional.of(record));

        WorkoutRecord result = service.findById(WORKOUT_ID, USER_ID);

        assertThat(result).isSameAs(record);
        verify(accessRegistry).checkAccess(AccessResourceType.WORKOUT, WORKOUT_ID, USER_ID);
    }

    @Test
    void findById_throwsNotFoundWhenRecordMissing() {
        when(repo.findById(WORKOUT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(WORKOUT_ID, USER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void findById_throwsAccessDeniedWhenNotOwner() {
        doThrow(new AccessDeniedException("denied"))
                .when(accessRegistry).checkAccess(AccessResourceType.WORKOUT, WORKOUT_ID, USER_ID);

        assertThatThrownBy(() -> service.findById(WORKOUT_ID, USER_ID))
                .isInstanceOf(AccessDeniedException.class);

        verify(repo, never()).findById(anyLong());
    }

    @Test
    void findAll_delegatesToRepository() {
        List<WorkoutRecord> expected = List.of(new WorkoutRecord());
        when(repo.findByParent(eq(USER_ID), any())).thenReturn(expected);

        List<WorkoutRecord> result = service.findAll(USER_ID, 0, 20);

        assertThat(result).isSameAs(expected);
    }

    @Test
    void update_appliesNonNullFieldsAndStores() {
        WorkoutRecord record = mock(WorkoutRecord.class);
        when(repo.findById(WORKOUT_ID)).thenReturn(Optional.of(record));
        OffsetDateTime performedAt = OffsetDateTime.now();
        EditWorkoutRequest req = new EditWorkoutRequest(WORKOUT_ID, "New name", performedAt);

        service.update(req, USER_ID);

        verify(record).setName("New name");
        verify(record).setPerformedAt(performedAt);
        verify(record).store();
    }

    @Test
    void update_skipsNullFields() {
        WorkoutRecord record = mock(WorkoutRecord.class);
        when(repo.findById(WORKOUT_ID)).thenReturn(Optional.of(record));
        EditWorkoutRequest req = new EditWorkoutRequest(WORKOUT_ID, null, null);

        service.update(req, USER_ID);

        verify(record, never()).setName(any());
        verify(record, never()).setPerformedAt(any());
        verify(record).store();
    }

    @Test
    void deleteById_checksAccessThenDeletes() {
        service.deleteById(WORKOUT_ID, USER_ID);

        verify(accessRegistry).checkAccess(AccessResourceType.WORKOUT, WORKOUT_ID, USER_ID);
        verify(repo).deleteById(WORKOUT_ID);
    }

    @Test
    void deleteById_throwsAccessDeniedAndDoesNotDelete() {
        doThrow(new AccessDeniedException("denied"))
                .when(accessRegistry).checkAccess(AccessResourceType.WORKOUT, WORKOUT_ID, USER_ID);

        assertThatThrownBy(() -> service.deleteById(WORKOUT_ID, USER_ID))
                .isInstanceOf(AccessDeniedException.class);

        verify(repo, never()).deleteById(anyLong());
    }
}
