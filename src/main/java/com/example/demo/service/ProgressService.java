package com.example.demo.service;

import com.example.demo.entity.Progress;
import com.example.demo.entity.User;
import com.example.demo.repository.ProgressRepository;
import org.springframework.stereotype.Service;

@Service
public class ProgressService extends CrudService<Progress, ProgressRepository, Long> {
    @Override
    public boolean hasUser(Progress entry, User user) {
        return false;
    }
}
