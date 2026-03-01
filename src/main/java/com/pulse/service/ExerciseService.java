package com.pulse.service;

import com.pulse.entity.Exercise;
import com.pulse.entity.ExerciseType;
import com.pulse.entity.Workout;
import com.pulse.entity.dto.ExerciseDto;
import com.pulse.repository.ExerciseRepository;
import com.pulse.repository.ExerciseTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseService extends CrudService<Exercise, ExerciseRepository, Long> {
    private final ExerciseTypeRepository exerciseTypeRepository;

    public void save(Workout workout, ExerciseDto dto) {
        ExerciseType exerciseType = findExerciseTypeByName(dto.name());
        super.save(new Exercise(workout, exerciseType));
    }

    public void update(Long exerciseId, ExerciseDto dto) {
        Exercise exercise = findById(exerciseId);
        exercise.setExerciseType(findExerciseTypeByName(dto.name()));
        super.update(exerciseId, exercise);
    }

    @Override
    public boolean hasUser(Long exerciseId, Long userId) {
        return repository.existsByIdAndWorkoutUserId(exerciseId, userId);
    }

    public boolean hasWorkoutUser(Long workoutId, Long userId) {
        return repository.existsByWorkoutIdAndWorkoutUserId(workoutId, userId);
    }

    @Transactional(readOnly = true)
    private ExerciseType findExerciseTypeByName(String name) {
        return exerciseTypeRepository.findByNameIgnoreCase(name).orElseThrow(
                () -> new EntityNotFoundException("Exercise with name " + name + " not found")
        );
    }

    public List<Exercise> findExercisesByWorkoutId(Long workoutId) {
        return repository.findExercisesByWorkoutId(workoutId);
    }
}
