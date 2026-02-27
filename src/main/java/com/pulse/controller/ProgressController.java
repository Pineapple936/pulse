package com.pulse.controller;

import com.pulse.entity.Progress;
import com.pulse.entity.dto.ProgressDetailsDto;
import com.pulse.entity.User;
import com.pulse.entity.dto.ProgressEditDto;
import com.pulse.entity.dto.response.ResponseMessageDto;
import com.pulse.service.ExerciseService;
import com.pulse.service.ProgressService;
import com.pulse.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {
    private final ProgressService progressService;
    private final ExerciseService exerciseService;

    @GetMapping("/{progressId}")
    public ResponseEntity<?> findProgressById(@AuthenticationPrincipal User user,
                                                     @PathVariable Long progressId) {
        Progress progress = progressService.findById(progressId);
        if(!progressService.hasUser(progress, user))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorizedUser());
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/exercise/{exerciseId}")
    public ResponseEntity<?> findAllProgressByExerciseId(@PathVariable Long exerciseId,
                                                         @AuthenticationPrincipal User user) {
        if(!exerciseService.hasUser(exerciseService.findById(exerciseId), user))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorizedUser());
        return ResponseEntity.ok(progressService.findAllByExerciseId(exerciseId));
    }

    @PostMapping
    public ResponseEntity<ResponseMessageDto> createProgress(@RequestBody ProgressDetailsDto dto,
                                                             @AuthenticationPrincipal User user) {
        if(!progressService.hasUser(new Progress(
                dto.repetitions(), dto.sets(), dto.weight(), exerciseService.findById(dto.exerciseId())
        ), user))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorizedUser());
        progressService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.createdMessage());
    }

    @PutMapping("/{progressId}")
    public ResponseEntity<ResponseMessageDto> editProgressById(@RequestBody ProgressEditDto dto,
                                                               @AuthenticationPrincipal User user,
                                                               @PathVariable Long progressId) {
        if(!progressService.hasUser(progressService.findById(progressId), user))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorizedUser());
        progressService.update(progressId, dto);
        return ResponseEntity.ok(ResponseUtil.updatedMessage());
    }

    @DeleteMapping("/{progressId}")
    public ResponseEntity<ResponseMessageDto> deleteProgressById(@AuthenticationPrincipal User user,
                                                                 @PathVariable Long progressId) {
        if(!progressService.hasUser(progressService.findById(progressId), user))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorizedUser());
        progressService.delete(progressId);
        return ResponseEntity.ok(ResponseUtil.deletedMessage());
    }
}
