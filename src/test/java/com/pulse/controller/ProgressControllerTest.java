package com.pulse.controller;

import com.pulse.entity.Progress;
import com.pulse.entity.User;
import com.pulse.entity.dto.ProgressDetailsDto;
import com.pulse.entity.dto.response.ResponseMessageDto;
import com.pulse.service.ExerciseService;
import com.pulse.service.ProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProgressControllerTest {

    @Mock
    private ProgressService progressService;

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private ProgressController controller;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
    }

    @Test
    void findProgressById_authorized() {
        Long id = 4L;
        Progress prog = new Progress();
        when(progressService.findById(id)).thenReturn(prog);
        ResponseEntity<Progress> resp = controller.findProgressById(user, id);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(prog, resp.getBody());
    }

    @Test
    void findAllProgressByExerciseId_authorized() {
        Long exId = 3L;
        when(progressService.findAllByExerciseId(exId)).thenReturn(Collections.singletonList(new Progress()));
        ResponseEntity<List<Progress>> resp = controller.findAllProgressByExerciseId(exId, user);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void createProgress_authorized() {
        Long exId = 8L;
        ProgressDetailsDto dto = new ProgressDetailsDto(1,1,1.0f);
        when(exerciseService.findById(exId)).thenReturn(new com.pulse.entity.Exercise());
        ResponseEntity<ResponseMessageDto> resp = controller.createProgress(dto, user, exId);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        verify(progressService).save(any(), eq(dto));
    }

    @Test
    void editProgressById_authorized() {
        Long id = 12L;
        ProgressDetailsDto dto = new ProgressDetailsDto(3,4,5.0f);
        ResponseEntity<ResponseMessageDto> resp = controller.editProgressById(dto, user, id);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(progressService).update(id, dto);
    }

    @Test
    void deleteProgressById_authorized() {
        Long id = 20L;
        ResponseEntity<ResponseMessageDto> resp = controller.deleteProgressById(user, id);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(progressService).delete(id);
    }
}
