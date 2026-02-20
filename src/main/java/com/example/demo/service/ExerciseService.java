package com.example.demo.service;

import com.example.demo.entity.Exercise;
import com.example.demo.entity.ExerciseType;
import com.example.demo.entity.User;
import com.example.demo.entity.dto.ExerciseDto;
import com.example.demo.repository.ExerciseRepository;
import com.example.demo.repository.ExerciseTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private ExerciseType findExerciseTypeByName(String name) {
        return exerciseTypeRepository.findByNameIgnoreCase(name).orElseThrow(
                () -> new EntityNotFoundException("Exercise with name " + name + " not found")
        );
    }
}
