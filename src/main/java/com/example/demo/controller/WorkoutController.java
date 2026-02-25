package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.Workout;
import com.example.demo.entity.dto.response.ResponseMessageDto;
import com.example.demo.entity.dto.WorkoutDetailsDto;
import com.example.demo.service.WorkoutService;
import com.example.demo.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<?> findWorkoutById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Workout workout = workoutService.findById(id);
        if(!workoutService.hasUser(workout, user))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorizedUser());
        return ResponseEntity.ok(workout);
    }

    @PostMapping
    public ResponseEntity<ResponseMessageDto> createWorkout(@Valid @RequestBody WorkoutDetailsDto dto) {
        workoutService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.createdMessage());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessageDto> editWorkoutById(@PathVariable Long id,
                                                              @Valid @RequestBody WorkoutDetailsDto dto,
                                                              @AuthenticationPrincipal User user) {
        if(!workoutService.hasUser(workoutService.findById(id), user))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorizedUser());
        workoutService.update(id, dto);
        return ResponseEntity.ok(ResponseUtil.updatedMessage());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDto> deleteWorkoutById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if(!workoutService.hasUser(workoutService.findById(id), user))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorizedUser());
        workoutService.delete(id);
        return ResponseEntity.ok(ResponseUtil.deletedMessage());
    }
}
