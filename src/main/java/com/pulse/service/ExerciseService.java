package com.pulse.service;

import com.pulse.entity.Exercise;
import com.pulse.entity.ExerciseType;
import com.pulse.entity.User;
import com.pulse.entity.dto.ExerciseDto;
import com.pulse.repository.ExerciseRepository;
import com.pulse.repository.ExerciseTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExerciseService extends CrudService<Exercise, ExerciseRepository, Long> {
    private final ExerciseTypeRepository exerciseTypeRepository;
    private final WorkoutService workoutService;

    public void save(Long workoutId, ExerciseDto dto) {
        ExerciseType exerciseType = findExerciseTypeByName(dto.name());
        super.save(new Exercise(workoutService.findById(workoutId), exerciseType));
    }

    public void update(Long exerciseId, ExerciseDto dto) {
        Exercise exercise = findById(exerciseId);
        exercise.setExerciseType(findExerciseTypeByName(dto.name()));
        super.update(exerciseId, exercise);
    }

    @Override
    public boolean hasUser(Exercise exercise, User user) {
        return workoutService.hasUser(exercise.getWorkout(), user);
    }

    @Transactional(readOnly = true)
    private ExerciseType findExerciseTypeByName(String name) {
        return exerciseTypeRepository.findByNameIgnoreCase(name).orElseThrow(
                () -> new EntityNotFoundException("Exercise with name " + name + " not found")
        );
    }
}
