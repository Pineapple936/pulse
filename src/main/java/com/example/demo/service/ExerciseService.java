package com.example.demo.service;

import com.example.demo.entity.Exercise;
import com.example.demo.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

@Service
public class ExerciseService extends AbstractService<Exercise, Long> {
    ExerciseService(ExerciseRepository repository) {
        super(repository);
    }
}
