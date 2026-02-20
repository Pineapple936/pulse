package com.example.demo.controller;

import com.example.demo.entity.Workout;
import com.example.demo.entity.dto.ResponseMessageDto;
import com.example.demo.entity.dto.WorkoutDetailsDto;
import com.example.demo.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
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
    public ResponseEntity<ResponseMessageDto> createWorkout(@RequestBody WorkoutDetailsDto dto) {
        workoutService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessageDto("Workout created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessageDto> editWorkoutById(@PathVariable Long id, @RequestBody WorkoutDetailsDto dto) {
        if(!isWorkoutForCurrentUser(id))
            return UnAuthorizedUser();
        workoutService.update(id, dto);
        return ResponseEntity.ok(new ResponseMessageDto("Workout updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDto> deleteWorkoutById(@PathVariable Long id) {
        if(!isWorkoutForCurrentUser(id))
            return UnAuthorizedUser();
        workoutService.delete(id);
        return ResponseEntity.ok(new ResponseMessageDto("Workout deleted successfully"));
    }

    private boolean isWorkoutForCurrentUser(Long id) {
        return workoutService.hasCurrentUser(workoutService.findById(id));
    }

    private boolean isWorkoutForCurrentUser(Workout workout) {
        return workoutService.hasCurrentUser(workout);
    }

    private ResponseEntity<ResponseMessageDto> UnAuthorizedUser() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDto("You are not authorized"));
    }
}
