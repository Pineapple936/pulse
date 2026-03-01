package com.pulse.service;

import com.pulse.entity.User;
import com.pulse.entity.Workout;
import com.pulse.entity.dto.WorkoutDetailsDto;
import com.pulse.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutService extends CrudService<Workout, WorkoutRepository, Long> {

    @Transactional
    public List<Workout> getAllWorkoutsByUser(Long userId) {
        return repository.findAllByUserId(userId);
    }

    public void save(WorkoutDetailsDto dto, User user) {
        super.save(new Workout(user, dto));
    }

    @Transactional
    public void update(Long id, WorkoutDetailsDto dto) {
        Workout workout = findById(id);
        if(dto.name() != null && !dto.name().equals(workout.getName())) workout.setName(dto.name());
        if(dto.date() != null && !dto.date().equals(workout.getDate())) workout.setDate(dto.date());
        super.update(id, workout);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUser(Long id, Long userId) {
        return repository.existsByIdAndUserId(id, userId);
    }
}
