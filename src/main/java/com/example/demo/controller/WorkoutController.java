package com.example.demo.controller;

import com.example.demo.entity.Workout;
import com.example.demo.entity.dto.WorkoutDetailsDto;
import com.example.demo.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout")
@RequiredArgsConstructor
public class WorkoutController {
    private final WorkoutService workoutService;

    @GetMapping
    public ResponseEntity<List<Workout>> getAllWorkouts() {
        return ResponseEntity.ok(workoutService.getAllWorkoutsByUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findWorkoutById(@PathVariable Long id) {
        Workout workout = workoutService.findById(id);
        if(!isWorkoutForCurrentUser(workout))
            return UnAuthorizedUser();
        return ResponseEntity.ok(workout);
    }

    @PostMapping
    public ResponseEntity<Workout> createWorkout(@RequestBody WorkoutDetailsDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workoutService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editWorkoutById(@PathVariable Long id, @RequestBody WorkoutDetailsDto dto) {
        if(!isWorkoutForCurrentUser(id))
            return UnAuthorizedUser();
        return ResponseEntity.ok(workoutService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkoutById(@PathVariable Long id) {
        if(!isWorkoutForCurrentUser(id))
            return UnAuthorizedUser();
        workoutService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isWorkoutForCurrentUser(Long id) {
        return workoutService.hasCurrentUser(workoutService.findById(id));
    }

    private boolean isWorkoutForCurrentUser(Workout workout) {
        return workoutService.hasCurrentUser(workout);
    }

    private ResponseEntity<String> UnAuthorizedUser() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized.");
    }
}
