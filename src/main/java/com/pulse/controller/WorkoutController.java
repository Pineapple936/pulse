package com.pulse.controller;

import com.pulse.entity.User;
import com.pulse.entity.Workout;
import com.pulse.entity.dto.response.ResponseMessageDto;
import com.pulse.entity.dto.WorkoutDetailsDto;
import com.pulse.service.WorkoutService;
import com.pulse.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {
    private final WorkoutService workoutService;

    @GetMapping
    public ResponseEntity<List<Workout>> getAllWorkouts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(workoutService.getAllWorkoutsByUser(user.getId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@accessGuard.hasWorkoutAccess(#id, #user)")
    public ResponseEntity<?> findWorkoutById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(workoutService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ResponseMessageDto> createWorkout(@Valid @RequestBody WorkoutDetailsDto dto,
                                                            @AuthenticationPrincipal User user) {
        workoutService.save(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.createdMessage());
    }

    @PutMapping("/{id}")
    @PreAuthorize("@accessGuard.hasWorkoutAccess(#id, #user)")
    public ResponseEntity<ResponseMessageDto> editWorkoutById(@PathVariable Long id,
                                                              @Valid @RequestBody WorkoutDetailsDto dto,
                                                              @AuthenticationPrincipal User user) {
        workoutService.update(id, dto);
        return ResponseEntity.ok(ResponseUtil.updatedMessage());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@accessGuard.hasWorkoutAccess(#id, #user)")
    public ResponseEntity<ResponseMessageDto> deleteWorkoutById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        workoutService.delete(id);
        return ResponseEntity.ok(ResponseUtil.deletedMessage());
    }
}
