package com.pulse.repository;

import com.pulse.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByExerciseId(Long exerciseId);
}
