package com.pulse.service.workout;

import com.pulse.controller.workout.entity.request.CreateWorkoutRequest;
import com.pulse.controller.workout.entity.request.EditWorkoutRequest;
import com.pulse.jooq.tables.records.WorkoutRecord;
import com.pulse.repository.interfaces.FindableByParent;
import com.pulse.service.access.repository.AccessRepositoryRegistry;
import com.pulse.service.access.repository.entity.AccessResourceType;
import com.pulse.service.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkoutService {
    private final FindableByParent<WorkoutRecord> repo;
    private final AccessRepositoryRegistry accessRepositoryRegistry;

    public WorkoutRecord save(CreateWorkoutRequest req, Long userId) {
        WorkoutRecord record = new WorkoutRecord();
        record.setName(req.name());
        record.setPerformedAt(req.performedAt());
        record.setUserId(userId);
        record = repo.save(record);
        log.info("Saved workoutId={} by userId={}", record.getId(), userId);
        return record;
    }

    public WorkoutRecord findById(Long id, Long userId) {
        accessRepositoryRegistry.checkAccess(AccessResourceType.WORKOUT,  id, userId);
        WorkoutRecord record = repo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout with id=" + id + " not found")
        );
        log.info("Found workoutId={} for userId={}", id, userId);
        return record;
    }

    public List<WorkoutRecord> findAll(Long userId, int page, int size) {
        return repo.findByParent(userId, Page.of(page, size));
    }

    public void update(EditWorkoutRequest req, Long userId) {
        WorkoutRecord record = findById(req.id(), userId);
        if(req.name() != null) {
            record.setName(req.name());
        }
        if(req.performedAt() != null) {
            record.setPerformedAt(req.performedAt());
        }
        record.store();

        log.info("Updated workoutId={} by userId={}", req.id(), userId);
    }

    public void deleteById(Long id, Long userId) {
        accessRepositoryRegistry.checkAccess(AccessResourceType.WORKOUT,  id, userId);
        repo.deleteById(id);
        log.info("Deleted workoutId={} by userId={}", id, userId);
    }
}
