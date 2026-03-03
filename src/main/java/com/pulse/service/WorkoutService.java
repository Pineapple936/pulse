package com.pulse.service;

import com.pulse.entity.User;
import com.pulse.entity.Workout;
import com.pulse.entity.dto.WorkoutDetailsDto;
import com.pulse.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkoutService extends CrudService<Workout, WorkoutRepository, Long> {

    @Transactional
    public List<Workout> getAllWorkoutsByUser(Long userId) {
        log.debug("Find all workouts by userId={}", userId);
        return repository.findAllByUserId(userId);
    }

    public void save(WorkoutDetailsDto dto, User user) {
        log.info("Saving workout for userId={}", user.getId());
        super.save(new Workout(user, dto));
    }

    @Transactional
    public void update(Long id, WorkoutDetailsDto dto) {
        log.info("Updating workout id={}", id);
        Workout workout = findById(id);
        if(dto.name() != null && !dto.name().equals(workout.getName())) workout.setName(dto.name());
        if(dto.date() != null && !dto.date().equals(workout.getDate())) workout.setDate(dto.date());
        super.update(id, workout);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUser(Long id, Long userId) {
        log.debug("Check workout access workoutId={} userId={}", id, userId);
        return repository.existsByIdAndUserId(id, userId);
    }
}
