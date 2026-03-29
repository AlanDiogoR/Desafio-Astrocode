package com.astrocode.backend.api.controllers;

import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("SavingsGoalController - Integração")
class SavingsGoalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User savedUser;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        var user = User.builder()
                .name("Goal Test User")
                .email("goal.controller@" + UUID.randomUUID() + ".com")
                .password(passwordEncoder.encode("senha123"))
                .emailVerified(true)
                .build();
        savedUser = userRepository.save(user);
        authToken = obtainToken(savedUser.getEmail(), "senha123");
    }

    private String obtainToken(String email, String password) throws Exception {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", email);
        loginBody.put("password", password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }

    @Test
    @DisplayName("POST /api/goals com body válido → HTTP 201")
    void createGoal_success() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Meta Viagem");
        body.put("targetAmount", 5000);
        body.put("endDate", "2025-12-31");
        body.put("color", "#087f5b");

        mockMvc.perform(post("/api/goals")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Meta Viagem"))
                .andExpect(jsonPath("$.targetAmount").value(5000))
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("DELETE /api/goals/{id} → HTTP 204")
    void deleteGoal_success() throws Exception {
        Map<String, Object> createBody = new HashMap<>();
        createBody.put("name", "Meta para Excluir");
        createBody.put("targetAmount", 1000);
        createBody.put("endDate", null);
        createBody.put("color", null);

        MvcResult createResult = mockMvc.perform(post("/api/goals")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        String goalId = objectMapper.readTree(createResponse).get("id").asText();

        mockMvc.perform(delete("/api/goals/" + goalId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());
    }
}
