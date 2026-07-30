package com.pulse.service.exercise;

import com.pulse.controller.exercise.entity.request.CreateExerciseRequest;
import com.pulse.controller.exercise.entity.request.EditExerciseRequest;
import com.pulse.jooq.tables.records.ExerciseRecord;
import com.pulse.repository.interfaces.FindableByParent;
import com.pulse.service.access.repository.AccessRepositoryRegistry;
import com.pulse.service.access.repository.entity.AccessResourceType;
import com.pulse.service.pagination.Page;
import com.pulse.service.exercise.type.ExerciseTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExerciseService {
    private final FindableByParent<ExerciseRecord> repo;
    private final ExerciseTypeService exerciseTypeService;
    private final AccessRepositoryRegistry accessRepositoryRegistry;

    public ExerciseRecord save(CreateExerciseRequest req, Long userId) {
        accessRepositoryRegistry.checkAccess(AccessResourceType.WORKOUT, req.workoutId(), userId);
        if(!exerciseTypeService.existsById(req.exerciseTypeId())) {
            log.warn("Exercise type with id={} not found", req.exerciseTypeId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise type with id=" + req.exerciseTypeId() + " not found");
        }

        ExerciseRecord record = new ExerciseRecord();
        record.setWorkoutId(req.workoutId());
        record.setExerciseTypeId(req.exerciseTypeId());
        record.setDescription(req.description());
        record = repo.save(record);
        log.info("Saved id={} in workoutId={} by userId={}", record.getId(), req.workoutId(), userId);
        return record;
    }

    public ExerciseRecord findById(Long id, Long userId) {
        accessRepositoryRegistry.checkAccess(AccessResourceType.EXERCISE, id, userId);
        return repo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise with id=" + id + " not found")
        );
    }

    public List<ExerciseRecord> findExercisesByWorkoutId(Long workoutId, Long userId, int page, int size) {
        accessRepositoryRegistry.checkAccess(AccessResourceType.WORKOUT, workoutId, userId);
        return repo.findByParent(workoutId, Page.of(page, size));
    }

    public void update(EditExerciseRequest req, Long userId) {
        ExerciseRecord record = findById(req.id(), userId);
        if(req.exerciseTypeId() != null && exerciseTypeService.existsById(req.exerciseTypeId())) {
            record.setExerciseTypeId(req.exerciseTypeId());
        }
        record.setDescription(req.description());
        record.store();

        log.info("Updated id={} by userId={}", req.id(), userId);
    }

    public void deleteById(Long id, Long userId) {
        accessRepositoryRegistry.checkAccess(AccessResourceType.EXERCISE, id, userId);
        repo.deleteById(id);
        log.info("Deleted id={} by userId={}", id, userId);
    }
}
