package com.astrocode.backend.api.controllers;

import com.astrocode.backend.domain.entities.Subscription;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.model.enums.PlanType;
import com.astrocode.backend.domain.model.enums.SubscriptionStatus;
import com.astrocode.backend.domain.repositories.SubscriptionRepository;
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

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("SubscriptionController - Integração")
class SubscriptionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User savedUser;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        var user = User.builder()
                .name("Subscription Test User")
                .email("sub.controller@" + UUID.randomUUID() + ".com")
                .password(passwordEncoder.encode("senha123"))
                .build();
        savedUser = userRepository.save(user);

        var sub = Subscription.builder()
                .user(savedUser)
                .planType(PlanType.FREE)
                .status(SubscriptionStatus.ACTIVE)
                .build();
        subscriptionRepository.save(sub);

        authToken = obtainAuthToken();
    }

    private String obtainAuthToken() throws Exception {
        String body = String.format("{\"email\":\"%s\",\"password\":\"senha123\"}", savedUser.getEmail());
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        String setCookie = result.getResponse().getHeader("Set-Cookie");
        if (setCookie != null && setCookie.contains("auth_token=")) {
            String cookiePart = setCookie.split(";")[0];
            return cookiePart.substring("auth_token=".length());
        }
        throw new IllegalStateException("Token não encontrado");
    }

    @Test
    @DisplayName("GET /api/subscription/plans retorna lista de planos (público)")
    void listPlansPublic() throws Exception {
        mockMvc.perform(get("/api/subscription/plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(4)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].price").exists());
    }

    @Test
    @DisplayName("GET /api/subscription/me retorna assinatura quando autenticado")
    void getMeWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/subscription/me")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planType").value("FREE"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/subscription/me retorna 401 quando não autenticado")
    void getMeWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/subscription/me"))
                .andExpect(status().isUnauthorized());
    }
}
