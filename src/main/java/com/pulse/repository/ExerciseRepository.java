package com.pulse.repository;

import com.pulse.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    boolean existsByIdAndWorkoutUserId(Long exerciseId, Long userId);

    @Query("SELECT e FROM Exercise e WHERE e.workout.id = :workoutId")
    List<Exercise> findExercisesByWorkoutId(@Param("workoutId") Long workoutId);
}
