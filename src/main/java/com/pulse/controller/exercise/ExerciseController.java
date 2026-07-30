package com.pulse.controller.exercise;

import com.pulse.controller.exercise.entity.ExerciseMapper;
import com.pulse.controller.exercise.entity.request.CreateExerciseRequest;
import com.pulse.controller.exercise.entity.request.EditExerciseRequest;
import com.pulse.controller.exercise.entity.response.ExerciseResponse;
import com.pulse.repository.user.entity.UserDetailsImpl;
import com.pulse.service.exercise.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/exercises")
public class ExerciseController {
    private final ExerciseService service;
    private final ExerciseMapper mapper;

    @PostMapping
    public ResponseEntity<ExerciseResponse> createExerciseInWorkout(@Valid @RequestBody CreateExerciseRequest req,
                                                                    @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Create exercise in workoutId={} userId={}", req.workoutId(), user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(service.save(req, user.getId())));
    }

    @GetMapping("/{exerciseId}")
    public ResponseEntity<ExerciseResponse> findById(@PathVariable Long exerciseId,
                                                   @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Find exercise with id={}", exerciseId);
        return ResponseEntity.ok(mapper.toResponse(service.findById(exerciseId, user.getId())));
    }

    @GetMapping("/workout/{workoutId}")
    public ResponseEntity<List<ExerciseResponse>> findAllByWorkoutId(@PathVariable Long workoutId,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "20") int size,
                                                                   @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Get exercises for workoutId={} userId={} on page={} with size={}", workoutId, user.getId(), page, size);
        return ResponseEntity.ok(service.findExercisesByWorkoutId(workoutId, user.getId(), page, size).stream()
                .map(mapper::toResponse).toList());
    }

    @PutMapping
    public ResponseEntity<Void> editExerciseById(@Valid @RequestBody EditExerciseRequest req,
                                                 @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Update id={} userId={}", req.id(), user.getId());
        service.update(req, user.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<Void> deleteExerciseById(@PathVariable Long exerciseId,
                                                   @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Delete id={} userId={}", exerciseId, user.getId());
        service.deleteById(exerciseId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
