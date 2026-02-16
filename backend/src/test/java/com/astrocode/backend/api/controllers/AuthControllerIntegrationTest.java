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
    @DisplayName("POST /api/auth/login deve retornar token quando credenciais válidas")
    void loginShouldReturnTokenWhenValid() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", savedUser.getEmail());
        body.put("password", "senha123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(blankOrNullString())))
                .andExpect(jsonPath("$.name").value("Controller Test User"));
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
