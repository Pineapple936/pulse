package com.pulse.service.exercise;

import com.pulse.controller.exercise.entity.request.CreateExerciseRequest;
import com.pulse.controller.exercise.entity.request.EditExerciseRequest;
import com.pulse.jooq.tables.records.ExerciseRecord;
import com.pulse.repository.interfaces.FindableByParent;
import com.pulse.service.access.repository.AccessRepositoryRegistry;
import com.pulse.service.access.repository.entity.AccessResourceType;
import com.pulse.service.exercise.type.ExerciseTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

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
class ExerciseServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long WORKOUT_ID = 10L;
    private static final Long EXERCISE_ID = 20L;
    private static final Long EXERCISE_TYPE_ID = 30L;

    @Mock
    private FindableByParent<ExerciseRecord> repo;
    @Mock
    private ExerciseTypeService exerciseTypeService;
    @Mock
    private AccessRepositoryRegistry accessRegistry;

    @InjectMocks
    private ExerciseService service;

    @Test
    void save_persistsWhenAccessGrantedAndTypeExists() {
        CreateExerciseRequest req = new CreateExerciseRequest(WORKOUT_ID, EXERCISE_TYPE_ID, "Squats");
        when(exerciseTypeService.existsById(EXERCISE_TYPE_ID)).thenReturn(true);
        ExerciseRecord saved = new ExerciseRecord(EXERCISE_ID, WORKOUT_ID, EXERCISE_TYPE_ID, "Squats", null);
        when(repo.save(any(ExerciseRecord.class))).thenReturn(saved);

        ExerciseRecord result = service.save(req, USER_ID);

        assertThat(result).isSameAs(saved);
        verify(accessRegistry).checkAccess(AccessResourceType.WORKOUT, WORKOUT_ID, USER_ID);
    }

    @Test
    void save_throwsNotFoundWhenExerciseTypeMissing() {
        CreateExerciseRequest req = new CreateExerciseRequest(WORKOUT_ID, EXERCISE_TYPE_ID, "Squats");
        when(exerciseTypeService.existsById(EXERCISE_TYPE_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.save(req, USER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Exercise type");

        verify(repo, never()).save(any());
    }

    @Test
    void save_throwsAccessDeniedWhenWorkoutNotOwned() {
        CreateExerciseRequest req = new CreateExerciseRequest(WORKOUT_ID, EXERCISE_TYPE_ID, "Squats");
        doThrow(new AccessDeniedException("denied"))
                .when(accessRegistry).checkAccess(AccessResourceType.WORKOUT, WORKOUT_ID, USER_ID);

        assertThatThrownBy(() -> service.save(req, USER_ID))
                .isInstanceOf(AccessDeniedException.class);

        verify(repo, never()).save(any());
    }

    @Test
    void findById_returnsRecordWhenFound() {
        ExerciseRecord record = new ExerciseRecord(EXERCISE_ID, WORKOUT_ID, EXERCISE_TYPE_ID, "Squats", null);
        when(repo.findById(EXERCISE_ID)).thenReturn(Optional.of(record));

        ExerciseRecord result = service.findById(EXERCISE_ID, USER_ID);

        assertThat(result).isSameAs(record);
        verify(accessRegistry).checkAccess(AccessResourceType.EXERCISE, EXERCISE_ID, USER_ID);
    }

    @Test
    void findById_throwsNotFoundWhenMissing() {
        when(repo.findById(EXERCISE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(EXERCISE_ID, USER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void findExercisesByWorkoutId_delegatesToRepository() {
        List<ExerciseRecord> expected = List.of(new ExerciseRecord());
        when(repo.findByParent(eq(WORKOUT_ID), any())).thenReturn(expected);

        List<ExerciseRecord> result = service.findExercisesByWorkoutId(WORKOUT_ID, USER_ID, 0, 20);

        assertThat(result).isSameAs(expected);
        verify(accessRegistry).checkAccess(AccessResourceType.WORKOUT, WORKOUT_ID, USER_ID);
    }

    @Test
    void update_appliesTypeAndDescriptionWhenValid() {
        ExerciseRecord record = mock(ExerciseRecord.class);
        when(repo.findById(EXERCISE_ID)).thenReturn(Optional.of(record));
        when(exerciseTypeService.existsById(EXERCISE_TYPE_ID)).thenReturn(true);
        EditExerciseRequest req = new EditExerciseRequest(EXERCISE_ID, EXERCISE_TYPE_ID, "Lunges");

        service.update(req, USER_ID);

        verify(record).setExerciseTypeId(EXERCISE_TYPE_ID);
        verify(record).setDescription("Lunges");
        verify(record).store();
    }

    @Test
    void update_clearsDescriptionAndIgnoresUnknownType() {
        ExerciseRecord record = mock(ExerciseRecord.class);
        when(repo.findById(EXERCISE_ID)).thenReturn(Optional.of(record));
        when(exerciseTypeService.existsById(EXERCISE_TYPE_ID)).thenReturn(false);
        EditExerciseRequest req = new EditExerciseRequest(EXERCISE_ID, EXERCISE_TYPE_ID, null);

        service.update(req, USER_ID);

        verify(record, never()).setExerciseTypeId(anyLong());
        verify(record).setDescription(null);
        verify(record).store();
    }

    @Test
    void deleteById_checksAccessThenDeletes() {
        service.deleteById(EXERCISE_ID, USER_ID);

        verify(accessRegistry).checkAccess(AccessResourceType.EXERCISE, EXERCISE_ID, USER_ID);
        verify(repo).deleteById(EXERCISE_ID);
    }
}
