package com.pulse.repository;

import com.pulse.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByExerciseId(Long exerciseId);

    boolean existsByIdAndExerciseWorkoutUserId(Long progressId, Long userId);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Exercise e WHERE e.id = :exerciseId AND e.workout.user.id = :userId")
    boolean existsExerciseByIdAndUserId(@Param("exerciseId") Long exerciseId, @Param("userId") Long userId);
}
