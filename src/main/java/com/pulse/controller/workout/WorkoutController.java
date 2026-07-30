package com.pulse.controller.workout;

import com.pulse.controller.workout.entity.WorkoutMapper;
import com.pulse.controller.workout.entity.request.CreateWorkoutRequest;
import com.pulse.controller.workout.entity.request.EditWorkoutRequest;
import com.pulse.controller.workout.entity.response.WorkoutResponse;
import com.pulse.repository.user.entity.UserDetailsImpl;
import com.pulse.service.workout.WorkoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@Slf4j
@RequiredArgsConstructor
public class WorkoutController {
    private final WorkoutService service;
    private final WorkoutMapper mapper;

    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(@Valid @RequestBody CreateWorkoutRequest req,
                                                         @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Create workout for userId={}", user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(service.save(req, user.getId())));
    }

    @GetMapping
    public ResponseEntity<List<WorkoutResponse>> getAllWorkouts(@AuthenticationPrincipal UserDetailsImpl user,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "20") int size) {
        log.info("Get workouts for userId={} page={} size={}", user.getId(), page, size);
        return ResponseEntity.ok(service.findAll(user.getId(), page, size).stream()
                .map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponse> findById(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Get workout id={} for userId={}", id, user.getId());
        return ResponseEntity.ok(mapper.toResponse(service.findById(id, user.getId())));
    }

    @PutMapping
    public ResponseEntity<Void> editWorkoutById(@Valid @RequestBody EditWorkoutRequest req,
                                                         @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Update workout id={} for userId={}", req.id(), user.getId());
        service.update(req, user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkoutById(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Delete workout id={} for userId={}", id, user.getId());
        service.deleteById(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
