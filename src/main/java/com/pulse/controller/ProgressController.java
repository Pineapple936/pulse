package com.pulse.controller;

import com.pulse.entity.dto.ProgressDetailsDto;
import com.pulse.entity.User;
import com.pulse.entity.dto.response.ResponseMessageDto;
import com.pulse.service.ExerciseService;
import com.pulse.service.ProgressService;
import com.pulse.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {
    private final ProgressService progressService;
    private final ExerciseService exerciseService;

    @GetMapping("/{progressId}")
    @PreAuthorize("@accessGuard.hasProgressAccess(#progressId, #user)")
    public ResponseEntity<?> findProgressById(@AuthenticationPrincipal User user,
                                              @PathVariable Long progressId) {
        return ResponseEntity.ok(progressService.findById(progressId));
    }

    @GetMapping("/exercise/{exerciseId}")
    @PreAuthorize("@accessGuard.hasExerciseProgressAccess(#exerciseId, #user)")
    public ResponseEntity<?> findAllProgressByExerciseId(@PathVariable Long exerciseId,
                                                         @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(progressService.findAllByExerciseId(exerciseId));
    }

    @PostMapping("/{exerciseId}")
    @PreAuthorize("@accessGuard.hasExerciseProgressAccess(#exerciseId, #user)")
    public ResponseEntity<ResponseMessageDto> createProgress(@RequestBody ProgressDetailsDto dto,
                                                             @AuthenticationPrincipal User user,
                                                             @PathVariable Long exerciseId) {
        progressService.save(exerciseService.findById(exerciseId), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.createdMessage());
    }

    @PutMapping("/{progressId}")
    @PreAuthorize("@accessGuard.hasProgressAccess(#progressId, #user)")
    public ResponseEntity<ResponseMessageDto> editProgressById(@RequestBody ProgressDetailsDto dto,
                                                               @AuthenticationPrincipal User user,
                                                               @PathVariable Long progressId) {
        progressService.update(progressId, dto);
        return ResponseEntity.ok(ResponseUtil.updatedMessage());
    }

    @DeleteMapping("/{progressId}")
    @PreAuthorize("@accessGuard.hasProgressAccess(#progressId, #user)")
    public ResponseEntity<ResponseMessageDto> deleteProgressById(@AuthenticationPrincipal User user,
                                                                 @PathVariable Long progressId) {
        progressService.delete(progressId);
        return ResponseEntity.ok(ResponseUtil.deletedMessage());
    }
}
