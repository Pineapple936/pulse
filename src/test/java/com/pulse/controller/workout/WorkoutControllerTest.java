package com.pulse.controller.workout;

import com.pulse.controller.error.GlobalExceptionHandler;
import com.pulse.controller.workout.entity.WorkoutMapper;
import com.pulse.controller.workout.entity.response.WorkoutResponse;
import com.pulse.jooq.tables.records.WorkoutRecord;
import com.pulse.repository.user.entity.UserDetailsImpl;
import com.pulse.service.workout.WorkoutService;
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

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
class WorkoutControllerTest {

    private static final Long USER_ID = 1L;
    private static final Long WORKOUT_ID = 10L;

    @Mock
    private WorkoutService service;
    @Mock
    private WorkoutMapper mapper;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new WorkoutController(service, mapper))
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

    private WorkoutResponse response() {
        return new WorkoutResponse(WORKOUT_ID, USER_ID, "Leg day", OffsetDateTime.parse("2026-01-01T10:00:00Z"));
    }

    @Test
    void create_returns201() throws Exception {
        when(service.save(any(), eq(USER_ID))).thenReturn(new WorkoutRecord());
        when(mapper.toResponse(any())).thenReturn(response());
        String body = """
                {"name":"Leg day","performedAt":"2026-01-01T10:00:00Z"}""";

        mvc.perform(post("/api/workouts").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Leg day"));
    }

    @Test
    void getAll_returns200() throws Exception {
        when(service.findAll(eq(USER_ID), anyInt(), anyInt())).thenReturn(List.of(new WorkoutRecord()));
        when(mapper.toResponse(any())).thenReturn(response());

        mvc.perform(get("/api/workouts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(WORKOUT_ID));
    }

    @Test
    void findById_returns200() throws Exception {
        when(service.findById(WORKOUT_ID, USER_ID)).thenReturn(new WorkoutRecord());
        when(mapper.toResponse(any())).thenReturn(response());

        mvc.perform(get("/api/workouts/{id}", WORKOUT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(WORKOUT_ID));
    }

    @Test
    void findById_returns404WhenMissing() throws Exception {
        when(service.findById(WORKOUT_ID, USER_ID))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout with id=10 not found"));

        mvc.perform(get("/api/workouts/{id}", WORKOUT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_returns403WhenNotOwner() throws Exception {
        when(service.findById(WORKOUT_ID, USER_ID)).thenThrow(new AccessDeniedException("denied"));

        mvc.perform(get("/api/workouts/{id}", WORKOUT_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    void edit_returns200() throws Exception {
        doNothing().when(service).update(any(), eq(USER_ID));
        String body = """
                {"id":10,"name":"New name"}""";

        mvc.perform(put("/api/workouts").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }

    @Test
    void edit_returns404WhenMissing() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"))
                .when(service).update(any(), eq(USER_ID));
        String body = """
                {"id":10,"name":"New name"}""";

        mvc.perform(put("/api/workouts").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns204() throws Exception {
        doNothing().when(service).deleteById(WORKOUT_ID, USER_ID);

        mvc.perform(delete("/api/workouts/{id}", WORKOUT_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_returns403WhenNotOwner() throws Exception {
        doThrow(new AccessDeniedException("denied")).when(service).deleteById(anyLong(), eq(USER_ID));

        mvc.perform(delete("/api/workouts/{id}", WORKOUT_ID))
                .andExpect(status().isForbidden());
    }
}
