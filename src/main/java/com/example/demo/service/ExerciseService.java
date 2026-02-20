package com.example.demo.service;

import com.example.demo.entity.Exercise;
import com.example.demo.entity.User;
import com.example.demo.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

@Service
public class ExerciseService extends CrudService<Exercise, ExerciseRepository, Long> {
    @Override
    public boolean hasUser(Exercise entry, User user) {
        return false;
    }
}
