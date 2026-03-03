package com.pulse.controller;

import com.pulse.entity.dto.ProgressDetailsDto;
import com.pulse.entity.Progress;
import com.pulse.entity.User;
import com.pulse.entity.dto.response.ResponseMessageDto;
import com.pulse.service.ExerciseService;
import com.pulse.service.ProgressService;
import com.pulse.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
@Slf4j
@RequiredArgsConstructor
public class ProgressController {
    private final ProgressService progressService;
    private final ExerciseService exerciseService;

    @GetMapping("/{progressId}")
    @PreAuthorize("@accessGuard.hasProgressAccess(#progressId, #user)")
    public ResponseEntity<Progress> findProgressById(@AuthenticationPrincipal User user,
                                              @PathVariable Long progressId) {
        log.info("Get progressId={} userId={}", progressId, user.getId());
        return ResponseEntity.ok(progressService.findById(progressId));
    }

    @GetMapping("/exercise/{exerciseId}")
    @PreAuthorize("@accessGuard.hasExerciseProgressAccess(#exerciseId, #user)")
    public ResponseEntity<List<Progress>> findAllProgressByExerciseId(@PathVariable Long exerciseId,
                                                         @AuthenticationPrincipal User user) {
        log.info("Get progress list by exerciseId={} userId={}", exerciseId, user.getId());
        return ResponseEntity.ok(progressService.findAllByExerciseId(exerciseId));
    }

    @PostMapping("/{exerciseId}")
    @PreAuthorize("@accessGuard.hasExerciseProgressAccess(#exerciseId, #user)")
    public ResponseEntity<ResponseMessageDto> createProgress(@RequestBody ProgressDetailsDto dto,
                                                             @AuthenticationPrincipal User user,
                                                             @PathVariable Long exerciseId) {
        log.info("Create progress for exerciseId={} userId={}", exerciseId, user.getId());
        progressService.save(exerciseService.findById(exerciseId), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.createdMessage());
    }

    @PutMapping("/{progressId}")
    @PreAuthorize("@accessGuard.hasProgressAccess(#progressId, #user)")
    public ResponseEntity<ResponseMessageDto> editProgressById(@RequestBody ProgressDetailsDto dto,
                                                               @AuthenticationPrincipal User user,
                                                               @PathVariable Long progressId) {
        log.info("Update progressId={} userId={}", progressId, user.getId());
        progressService.update(progressId, dto);
        return ResponseEntity.ok(ResponseUtil.updatedMessage());
    }

    @DeleteMapping("/{progressId}")
    @PreAuthorize("@accessGuard.hasProgressAccess(#progressId, #user)")
    public ResponseEntity<ResponseMessageDto> deleteProgressById(@AuthenticationPrincipal User user,
                                                                 @PathVariable Long progressId) {
        log.info("Delete progressId={} userId={}", progressId, user.getId());
        progressService.delete(progressId);
        return ResponseEntity.ok(ResponseUtil.deletedMessage());
    }
}
