package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.Workout;
import com.example.demo.entity.dto.WorkoutDetailsDto;
import com.example.demo.repository.WorkoutRepository;
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

    public Workout save(WorkoutDetailsDto dto) {
        return super.save(new Workout(dto.name(), dto.date(), userService.getCurrentUser()));
    }

    public Workout update(Long id, WorkoutDetailsDto dto) {
        Workout workout = findById(id);
        if(dto.name() != null && !dto.name().equals(workout.getName())) workout.setName(dto.name());
        if(dto.date() != null && !dto.date().equals(workout.getDate())) workout.setDate(dto.date());
        return super.update(id, workout);
    }

    public boolean hasCurrentUser(Workout workout) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null || workout == null || workout.getUser() == null) {
            return false;
        }
        return currentUser.getId().equals(workout.getUser().getId());
    }
}
