package com.example.demo.service;

import com.example.demo.entity.Workout;
import com.example.demo.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkoutService extends AbstractService<Workout, Long> {
    WorkoutService(WorkoutRepository repository) {
        super(repository);
    }
}
