package com.pulse.controller;

import com.pulse.entity.User;
import com.pulse.entity.dto.ExerciseDto;
import com.pulse.entity.dto.response.ResponseMessageDto;
import com.pulse.service.ExerciseService;
import com.pulse.service.WorkoutService;
import com.pulse.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;
    private final WorkoutService workoutService;

    @GetMapping("/{workoutId}")
    @PreAuthorize("@accessGuard.hasExerciseWorkoutAccess(#workoutId, #user)")
    public ResponseEntity<?> findAllExercisesByWorkout(@PathVariable Long workoutId,
                                                       @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(exerciseService.findExercisesByWorkoutId(workoutId));
    }

    @PostMapping("/{workoutId}")
    @PreAuthorize("@accessGuard.hasExerciseWorkoutAccess(#workoutId, #user)")
    public ResponseEntity<ResponseMessageDto> createExerciseInWorkout(@PathVariable Long workoutId,
                                                                      @Valid @RequestBody ExerciseDto dto,
                                                                      @AuthenticationPrincipal User user) {
        exerciseService.save(workoutService.findById(workoutId), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.createdMessage());
    }

    @PutMapping("/{exerciseId}")
    @PreAuthorize("@accessGuard.hasExerciseAccess(#exerciseId, #user)")
    public ResponseEntity<ResponseMessageDto> editExerciseById(@PathVariable Long exerciseId,
                                                               @Valid @RequestBody ExerciseDto dto,
                                                               @AuthenticationPrincipal User user) {
        exerciseService.update(exerciseId, dto);
        return ResponseEntity.ok(ResponseUtil.updatedMessage());
    }

    @DeleteMapping("/{exerciseId}")
    @PreAuthorize("@accessGuard.hasExerciseAccess(#exerciseId, #user)")
    public ResponseEntity<ResponseMessageDto> deleteExerciseById(@PathVariable Long exerciseId,
                                                                 @AuthenticationPrincipal User user) {
        exerciseService.delete(exerciseId);
        return ResponseEntity.ok(ResponseUtil.deletedMessage());
    }
}
