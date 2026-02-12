package com.example.demo.repository;

import com.example.demo.entity.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseTypeRepository extends JpaRepository<ExerciseType, Long> {
}
