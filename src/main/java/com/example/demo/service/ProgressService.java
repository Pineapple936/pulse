package com.example.demo.service;

import com.example.demo.entity.Progress;
import com.example.demo.repository.ProgressRepository;
import org.springframework.stereotype.Service;

@Service
public class ProgressService extends AbstractService<Progress, Long> {
    ProgressService(ProgressRepository repository) {
        super(repository);
    }
}
