package com.pulse.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityFilterChainTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void protectedEndpoint_rejectsRequestWithoutToken() throws Exception {
        mvc.perform(get("/api/workouts"))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpoint_rejectsRequestWithInvalidToken() throws Exception {
        mvc.perform(get("/api/workouts").header(HttpHeaders.AUTHORIZATION, "Bearer not-a-real-token"))
                .andExpect(status().isForbidden());
    }
}
