package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.Workout;
import com.example.demo.entity.dto.ExerciseDto;
import com.example.demo.entity.dto.response.ResponseMessageDto;
import com.example.demo.service.ExerciseService;
import com.example.demo.service.WorkoutService;
import com.example.demo.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;
    private final WorkoutService workoutService;

    @GetMapping("/{workoutId}")
    public ResponseEntity<?> findAllExercisesByWorkout(@PathVariable Long workoutId,
                                                       @AuthenticationPrincipal User user) {
        Workout workout = workoutService.findById(workoutId);
        if(!workoutService.hasUser(workout, user))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorizedUser());
        return ResponseEntity.ok(workout.getExercises());
    }

    @PostMapping("/{workoutId}")
    public ResponseEntity<ResponseMessageDto> createExerciseInWorkout(@PathVariable Long workoutId,
                                                                      @Valid @RequestBody ExerciseDto dto,
                                                                      @AuthenticationPrincipal User user) {
        if(!workoutService.hasUser(workoutService.findById(workoutId), user))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorizedUser());
        exerciseService.save(workoutId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.createdMessage());
    }

    @PutMapping("/{exerciseId}")
    public ResponseEntity<ResponseMessageDto> editExerciseById(@PathVariable Long exerciseId,
                                                               @Valid @RequestBody ExerciseDto dto,
                                                               @AuthenticationPrincipal User user) {
        if(!workoutService.hasUser(exerciseService.findById(exerciseId).getWorkout(), user))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorizedUser());
        exerciseService.update(exerciseId, dto);
        return ResponseEntity.ok(ResponseUtil.updatedMessage());
    }

    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<ResponseMessageDto> deleteExerciseById(@PathVariable Long exerciseId,
                                                                 @AuthenticationPrincipal User user) {
        if(!workoutService.hasUser(exerciseService.findById(exerciseId).getWorkout(), user))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorizedUser());
        exerciseService.delete(exerciseId);
        return ResponseEntity.ok(ResponseUtil.deletedMessage());
    }
}
