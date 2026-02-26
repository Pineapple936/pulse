package com.pulse.service;

import com.pulse.entity.User;
import com.pulse.entity.Workout;
import com.pulse.entity.dto.WorkoutDetailsDto;
import com.pulse.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutService extends CrudService<Workout, WorkoutRepository, Long> {
    private final UserService userService;

    public List<Workout> getAllWorkoutsByUser() {
        return userService.getCurrentUser().getWorkouts();
    }

    public void save(WorkoutDetailsDto dto) {
        super.save(new Workout(dto.name(), dto.date(), userService.getCurrentUser()));
    }

    public void update(Long id, WorkoutDetailsDto dto) {
        Workout workout = findById(id);
        if(!dto.name().equals(workout.getName())) workout.setName(dto.name());
        if(!dto.date().equals(workout.getDate())) workout.setDate(dto.date());
        super.update(id, workout);
    }

    @Override
    public boolean hasUser(Workout entry, User user) {
        return user.getId().equals(entry.getUser().getId());
    }
}
