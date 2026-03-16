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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController - Integração")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User savedUser;

    @BeforeEach
    void setUp() {
        var user = User.builder()
                .name("Controller Test User")
                .email("controller.test@" + UUID.randomUUID() + ".com")
                .password(passwordEncoder.encode("senha123"))
                .build();
        savedUser = userRepository.save(user);
    }

    @Test
    @DisplayName("POST /api/auth/login deve retornar cookie auth_token quando credenciais válidas")
    void loginShouldReturnTokenWhenValid() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", savedUser.getEmail());
        body.put("password", "senha123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Controller Test User"))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("auth_token=")));
    }

    @Test
    @DisplayName("POST /api/auth/login deve retornar 401 quando senha incorreta")
    void loginShouldReturn401WhenWrongPassword() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", savedUser.getEmail());
        body.put("password", "senhaErrada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("OPTIONS /api/auth/login deve retornar 200 (preflight CORS)")
    void optionsLogin_returns200() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/auth/login deve retornar 401 quando email não existe")
    void loginShouldReturn401WhenEmailNotFound() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", "naoexiste@example.com");
        body.put("password", "senha123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }
}
