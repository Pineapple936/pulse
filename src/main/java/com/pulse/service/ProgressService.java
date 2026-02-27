package com.pulse.service;

import com.pulse.entity.Progress;
import com.pulse.entity.dto.ProgressDetailsDto;
import com.pulse.entity.User;
import com.pulse.entity.dto.ProgressEditDto;
import com.pulse.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressService extends CrudService<Progress, ProgressRepository, Long> {
    private final ExerciseService exerciseService;

    public List<Progress> findAllByExerciseId(Long exerciseId) {
        return repository.findByExerciseId(exerciseId);
    }

    public void save(ProgressDetailsDto dto) {
        super.save(new Progress(
                    dto.repetitions(), dto.sets(), dto.weight(), exerciseService.findById(dto.exerciseId())
                )
        );
    }

    @Transactional(readOnly = true)
    public void update(Long id, ProgressEditDto dto) {
        Progress progress = findById(id);
        if(dto.repetitions() != progress.getRepetitions()) progress.setRepetitions(dto.repetitions());
        if(dto.sets() != progress.getSets()) progress.setSets(dto.sets());
        if(dto.weight() != progress.getWeight()) progress.setWeight(dto.weight());
        super.save(progress);
    }

    @Override
    public boolean hasUser(Progress entry, User user) {
        return exerciseService.hasUser(entry.getExercise(), user);
    }
}
