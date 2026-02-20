package com.example.demo.repository;

import com.example.demo.entity.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseTypeRepository extends JpaRepository<ExerciseType, Long> {
    public Optional<ExerciseType> findByNameIgnoreCase(String name);
}
