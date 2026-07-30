package com.pulse.service.progress;

import com.pulse.controller.progress.entity.request.CreateProgressRequest;
import com.pulse.controller.progress.entity.request.EditProgressRequest;
import com.pulse.jooq.tables.records.ProgressRecord;
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
public class ProgressService {
    private final FindableByParent<ProgressRecord> repo;
    private final AccessRepositoryRegistry accessRepositoryRegistry;

    public ProgressRecord save(CreateProgressRequest req, Long userId) {
        accessRepositoryRegistry.checkAccess(AccessResourceType.EXERCISE, req.exerciseId(), userId);

        ProgressRecord record = new ProgressRecord();
        record.setExerciseId(req.exerciseId());
        record.setSetNumber(req.setNumber());
        record.setRepetition(req.repetition());
        record.setWeight(req.weight());
        record = repo.save(record);

        log.info("Saved id={} for id={} by userId={}", record.getId(), req.exerciseId(), userId);
        return record;
    }

    public ProgressRecord findById(Long id, Long userId) {
        accessRepositoryRegistry.checkAccess(AccessResourceType.PROGRESS, id, userId);
        return repo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Progress with id=" + id + " not found")
        );
    }

    public List<ProgressRecord> findAllByExerciseId(Long exerciseId, Long userId, int page, int size) {
        accessRepositoryRegistry.checkAccess(AccessResourceType.EXERCISE, exerciseId, userId);
        return repo.findByParent(exerciseId, Page.of(page, size));
    }

    public void update(EditProgressRequest req, Long userId) {
        ProgressRecord record = findById(req.id(), userId);
        if(req.repetition() != null) {
            record.setRepetition(req.repetition());
        }
        if(req.setNumber() != null) {
            record.setSetNumber(req.setNumber());
        }
        record.setWeight(req.weight());
        record.store();

        log.info("Updated id={} by userId={}", record.getId(), userId);
    }

    public void deleteById(Long id, Long userId) {
        accessRepositoryRegistry.checkAccess(AccessResourceType.PROGRESS, id, userId);
        repo.deleteById(id);
        log.info("Deleted id={}", id);
    }
}
