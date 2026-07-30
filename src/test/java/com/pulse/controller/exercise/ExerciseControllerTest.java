package com.pulse.controller.exercise;

import com.pulse.controller.error.GlobalExceptionHandler;
import com.pulse.controller.exercise.entity.ExerciseMapper;
import com.pulse.controller.exercise.entity.response.ExerciseResponse;
import com.pulse.jooq.tables.records.ExerciseRecord;
import com.pulse.repository.user.entity.UserDetailsImpl;
import com.pulse.service.exercise.ExerciseService;
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
class ExerciseControllerTest {

    private static final Long USER_ID = 1L;
    private static final Long WORKOUT_ID = 10L;
    private static final Long EXERCISE_ID = 20L;
    private static final Long EXERCISE_TYPE_ID = 30L;

    @Mock
    private ExerciseService service;
    @Mock
    private ExerciseMapper mapper;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ExerciseController(service, mapper))
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

    private ExerciseResponse response() {
        return new ExerciseResponse(EXERCISE_ID, WORKOUT_ID, EXERCISE_TYPE_ID, "Squats");
    }

    @Test
    void create_returns201() throws Exception {
        when(service.save(any(), eq(USER_ID))).thenReturn(new ExerciseRecord());
        when(mapper.toResponse(any())).thenReturn(response());
        String body = """
                {"workoutId":10,"exerciseTypeId":30,"description":"Squats"}""";

        mvc.perform(post("/api/exercises").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(EXERCISE_ID));
    }

    @Test
    void create_returns404WhenExerciseTypeMissing() throws Exception {
        when(service.save(any(), eq(USER_ID)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise type with id=30 not found"));
        String body = """
                {"workoutId":10,"exerciseTypeId":30,"description":"Squats"}""";

        mvc.perform(post("/api/exercises").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_returns403WhenWorkoutNotOwned() throws Exception {
        when(service.save(any(), eq(USER_ID))).thenThrow(new AccessDeniedException("denied"));
        String body = """
                {"workoutId":10,"exerciseTypeId":30,"description":"Squats"}""";

        mvc.perform(post("/api/exercises").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void findById_returns200() throws Exception {
        when(service.findById(EXERCISE_ID, USER_ID)).thenReturn(new ExerciseRecord());
        when(mapper.toResponse(any())).thenReturn(response());

        mvc.perform(get("/api/exercises/{id}", EXERCISE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EXERCISE_ID));
    }

    @Test
    void findById_returns404WhenMissing() throws Exception {
        when(service.findById(EXERCISE_ID, USER_ID))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mvc.perform(get("/api/exercises/{id}", EXERCISE_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_returns403WhenNotOwner() throws Exception {
        when(service.findById(EXERCISE_ID, USER_ID)).thenThrow(new AccessDeniedException("denied"));

        mvc.perform(get("/api/exercises/{id}", EXERCISE_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    void findAllByWorkoutId_returns200() throws Exception {
        when(service.findExercisesByWorkoutId(eq(WORKOUT_ID), eq(USER_ID), anyInt(), anyInt()))
                .thenReturn(List.of(new ExerciseRecord()));
        when(mapper.toResponse(any())).thenReturn(response());

        mvc.perform(get("/api/exercises/workout/{workoutId}", WORKOUT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(EXERCISE_ID));
    }

    @Test
    void edit_returns200() throws Exception {
        doNothing().when(service).update(any(), eq(USER_ID));
        String body = """
                {"id":20,"exerciseTypeId":30,"description":"Lunges"}""";

        mvc.perform(put("/api/exercises").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }

    @Test
    void edit_returns404WhenMissing() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"))
                .when(service).update(any(), eq(USER_ID));
        String body = """
                {"id":20,"exerciseTypeId":30,"description":"Lunges"}""";

        mvc.perform(put("/api/exercises").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns204() throws Exception {
        doNothing().when(service).deleteById(EXERCISE_ID, USER_ID);

        mvc.perform(delete("/api/exercises/{id}", EXERCISE_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_returns403WhenNotOwner() throws Exception {
        doThrow(new AccessDeniedException("denied")).when(service).deleteById(EXERCISE_ID, USER_ID);

        mvc.perform(delete("/api/exercises/{id}", EXERCISE_ID))
                .andExpect(status().isForbidden());
    }
}
