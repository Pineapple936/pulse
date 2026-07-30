package com.pulse.controller.progress;

import com.pulse.controller.error.GlobalExceptionHandler;
import com.pulse.controller.progress.entity.ProgressMapper;
import com.pulse.controller.progress.entity.response.ProgressResponse;
import com.pulse.jooq.tables.records.ProgressRecord;
import com.pulse.repository.user.entity.UserDetailsImpl;
import com.pulse.service.progress.ProgressService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProgressControllerTest {

    private static final Long USER_ID = 1L;
    private static final Long EXERCISE_ID = 20L;
    private static final Long PROGRESS_ID = 40L;

    @Mock
    private ProgressService service;
    @Mock
    private ProgressMapper mapper;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ProgressController(service, mapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        UserDetailsImpl principal = new UserDetailsImpl(USER_ID, "John", "john@example.com", "hash", null);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private ProgressResponse response() {
        return new ProgressResponse(PROGRESS_ID, EXERCISE_ID, 1, 10, new BigDecimal("50.00"));
    }

    @Test
    void create_returns201() throws Exception {
        when(service.save(any(), eq(USER_ID))).thenReturn(new ProgressRecord());
        when(mapper.toResponse(any())).thenReturn(response());
        String body = """
                {"exerciseId":20,"setNumber":1,"repetition":10,"weight":50.00}""";

        mvc.perform(post("/api/progress").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(PROGRESS_ID));
    }

    @Test
    void create_returns403WhenExerciseNotOwned() throws Exception {
        when(service.save(any(), eq(USER_ID))).thenThrow(new AccessDeniedException("denied"));
        String body = """
                {"exerciseId":20,"setNumber":1,"repetition":10,"weight":50.00}""";

        mvc.perform(post("/api/progress").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void findById_returns200() throws Exception {
        when(service.findById(PROGRESS_ID, USER_ID)).thenReturn(new ProgressRecord());
        when(mapper.toResponse(any())).thenReturn(response());

        mvc.perform(get("/api/progress/{id}", PROGRESS_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PROGRESS_ID));
    }

    @Test
    void findById_returns404WhenMissing() throws Exception {
        when(service.findById(PROGRESS_ID, USER_ID))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mvc.perform(get("/api/progress/{id}", PROGRESS_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_returns403WhenNotOwner() throws Exception {
        when(service.findById(PROGRESS_ID, USER_ID)).thenThrow(new AccessDeniedException("denied"));

        mvc.perform(get("/api/progress/{id}", PROGRESS_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    void findAllByExerciseId_returns200() throws Exception {
        when(service.findAllByExerciseId(eq(EXERCISE_ID), eq(USER_ID), anyInt(), anyInt()))
                .thenReturn(List.of(new ProgressRecord()));
        when(mapper.toResponse(any())).thenReturn(response());

        mvc.perform(get("/api/progress/exercise/{exerciseId}", EXERCISE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(PROGRESS_ID));
    }

    @Test
    void edit_returns200() throws Exception {
        doNothing().when(service).update(any(), eq(USER_ID));
        String body = """
                {"id":40,"repetition":12,"setNumber":3,"weight":60.00}""";

        mvc.perform(put("/api/progress").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }

    @Test
    void edit_returns400WhenValuesNotPositive() throws Exception {
        String body = """
                {"id":40,"repetition":-1,"setNumber":0,"weight":-5}""";

        mvc.perform(put("/api/progress").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void edit_returns404WhenMissing() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"))
                .when(service).update(any(), eq(USER_ID));
        String body = """
                {"id":40,"repetition":12,"setNumber":3,"weight":60.00}""";

        mvc.perform(put("/api/progress").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns204() throws Exception {
        doNothing().when(service).deleteById(PROGRESS_ID, USER_ID);

        mvc.perform(delete("/api/progress/{progressId}", PROGRESS_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_returns403WhenNotOwner() throws Exception {
        doThrow(new AccessDeniedException("denied")).when(service).deleteById(PROGRESS_ID, USER_ID);

        mvc.perform(delete("/api/progress/{progressId}", PROGRESS_ID))
                .andExpect(status().isForbidden());
    }
}
