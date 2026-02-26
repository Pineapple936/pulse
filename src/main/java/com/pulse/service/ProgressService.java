package com.pulse.service;

import com.pulse.entity.Progress;
import com.pulse.entity.User;
import com.pulse.repository.ProgressRepository;
import org.springframework.stereotype.Service;

@Service
public class ProgressService extends CrudService<Progress, ProgressRepository, Long> {
    @Override
    public boolean hasUser(Progress entry, User user) {
        return false;
    }
}
