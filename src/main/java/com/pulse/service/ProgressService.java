package com.pulse.service;

import com.pulse.entity.Exercise;
import com.pulse.entity.Progress;
import com.pulse.entity.dto.ProgressDetailsDto;
import com.pulse.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProgressService extends CrudService<Progress, ProgressRepository, Long> {

    public List<Progress> findAllByExerciseId(Long exerciseId) {
        log.debug("Find progress list by exerciseId={}", exerciseId);
        return repository.findByExerciseId(exerciseId);
    }

    public void save(Exercise exercise, ProgressDetailsDto dto) {
        log.info("Saving progress for exerciseId={}", exercise.getId());
        super.save(new Progress(exercise, dto));
    }

    @Transactional(readOnly = true)
    public void update(Long id, ProgressDetailsDto dto) {
        log.info("Updating progress id={}", id);
        Progress progress = findById(id);
        if(dto.repetitions() != progress.getRepetitions()) progress.setRepetitions(dto.repetitions());
        if(dto.sets() != progress.getSets()) progress.setSets(dto.sets());
        if(dto.weight() != progress.getWeight()) progress.setWeight(dto.weight());
        super.save(progress);
    }

    @Override
    public boolean hasUser(Long id, Long userId) {
        log.debug("Check progress access progressId={} userId={}", id, userId);
        return repository.existsByIdAndExerciseWorkoutUserId(id, userId);
    }

    public boolean hasExerciseUser(Long exerciseId, Long userId) {
        log.debug("Check progress-exercise access exerciseId={} userId={}", exerciseId, userId);
        return repository.existsExerciseByIdAndUserId(exerciseId, userId);
    }
}
