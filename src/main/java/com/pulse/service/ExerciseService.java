package com.pulse.service;

import com.pulse.entity.Exercise;
import com.pulse.entity.ExerciseType;
import com.pulse.entity.Workout;
import com.pulse.entity.dto.ExerciseDto;
import com.pulse.repository.ExerciseRepository;
import com.pulse.repository.ExerciseTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExerciseService extends CrudService<Exercise, ExerciseRepository, Long> {
    private final ExerciseTypeRepository exerciseTypeRepository;

    @Transactional
    public void save(Workout workout, ExerciseDto dto) {
        log.info("Saving exercise workoutId={} name={}", workout.getId(), dto.name());
        ExerciseType exerciseType = findExerciseTypeByName(dto.name());
        super.save(new Exercise(workout, exerciseType));
    }

    @Transactional
    public void update(Long exerciseId, ExerciseDto dto) {
        log.info("Updating exerciseId={} name={}", exerciseId, dto.name());
        Exercise exercise = findById(exerciseId);
        exercise.setExerciseType(findExerciseTypeByName(dto.name()));
        super.update(exerciseId, exercise);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUser(Long exerciseId, Long userId) {
        log.debug("Check exercise access exerciseId={} userId={}", exerciseId, userId);
        return repository.existsByIdAndWorkoutUserId(exerciseId, userId);
    }

    private ExerciseType findExerciseTypeByName(String name) {
        log.debug("Find exercise type by name={}", name);
        return exerciseTypeRepository.findByNameIgnoreCase(name).orElseThrow(
                () -> new EntityNotFoundException("Exercise with name " + name + " not found")
        );
    }

    @Transactional(readOnly = true)
    public List<Exercise> findExercisesByWorkoutId(Long workoutId) {
        log.debug("Find exercises by workoutId={}", workoutId);
        return repository.findExercisesByWorkoutId(workoutId);
    }
}
