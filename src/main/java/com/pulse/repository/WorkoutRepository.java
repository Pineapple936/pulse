package com.pulse.repository;

import com.pulse.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    boolean existsByIdAndUserId(Long workoutId, Long userId);

    public List<Workout> findAllByUserId(Long userId);
}
