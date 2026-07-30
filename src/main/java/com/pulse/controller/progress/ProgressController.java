package com.pulse.controller.progress;

import com.pulse.controller.progress.entity.ProgressMapper;
import com.pulse.controller.progress.entity.request.CreateProgressRequest;
import com.pulse.controller.progress.entity.request.EditProgressRequest;
import com.pulse.controller.progress.entity.response.ProgressResponse;
import com.pulse.repository.user.entity.UserDetailsImpl;
import com.pulse.service.progress.ProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
@Slf4j
@RequiredArgsConstructor
public class ProgressController {
    private final ProgressService service;
    private final ProgressMapper mapper;

    @PostMapping
    public ResponseEntity<ProgressResponse> createProgress(@Valid @RequestBody CreateProgressRequest req,
                                                           @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Create progress for id={} userId={}", req.exerciseId(), user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(service.save(req, user.getId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgressResponse> findProgressById(@PathVariable Long id,
                                                           @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Get id={}", id);
        return ResponseEntity.ok(mapper.toResponse(service.findById(id, user.getId())));
    }

    @GetMapping("/exercise/{exerciseId}")
    public ResponseEntity<List<ProgressResponse>> findAllProgressByExerciseId(@PathVariable Long exerciseId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "20") int size,
                                                                      @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Get progress list by id={} userId={} on page={} with size={}", exerciseId, user.getId(), page, size);
        return ResponseEntity.ok(service.findAllByExerciseId(exerciseId, user.getId(), page, size).stream()
                .map(mapper::toResponse).toList());
    }

    @PutMapping
    public ResponseEntity<Void> editProgressById(@Valid @RequestBody EditProgressRequest req,
                                                 @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Update id={} userId={}", req.id(), user.getId());
        service.update(req, user.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{progressId}")
    public ResponseEntity<Void> deleteProgressById(@AuthenticationPrincipal UserDetailsImpl user,
                                                   @PathVariable Long progressId) {
        log.info("Delete id={} by userId={}", progressId, user.getId());
        service.deleteById(progressId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
