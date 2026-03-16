package com.astrocode.backend.api.controllers;

import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.repositories.BankAccountRepository;
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

import java.math.BigDecimal;
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
@DisplayName("BankAccountController - Integração")
class BankAccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User savedUser;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        var user = User.builder()
                .name("Controller Test User")
                .email("bank.controller@" + UUID.randomUUID() + ".com")
                .password(passwordEncoder.encode("senha123"))
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
        return objectMapper.readTree(response).get("token").asText();
    }

    @Test
    @DisplayName("POST /api/accounts com body válido e token → HTTP 201 + conta criada")
    void createAccount_authenticated_success() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Conta Teste");
        body.put("initialBalance", 1000);
        body.put("type", "CHECKING");
        body.put("color", "#087f5b");

        mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Conta Teste"))
                .andExpect(jsonPath("$.currentBalance").value(1000))
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("POST /api/accounts com nome duplicado → HTTP 409")
    void createAccount_duplicateName_conflict() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Conta Duplicada");
        body.put("initialBalance", 500);
        body.put("type", "CASH");
        body.put("color", null);

        mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/accounts sem autenticação → HTTP 401")
    void createAccount_unauthorized() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Conta");
        body.put("initialBalance", 0);
        body.put("type", "CHECKING");
        body.put("color", null);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/accounts autenticado → HTTP 200 + lista de contas")
    void getAll_authenticated_success() throws Exception {
        mockMvc.perform(get("/api/accounts")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("DELETE /api/accounts/{id} com conta de outro usuário → HTTP 403")
    void deleteAccount_otherUser_forbidden() throws Exception {
        var otherUser = User.builder()
                .name("Other")
                .email("other@" + UUID.randomUUID() + ".com")
                .password(passwordEncoder.encode("x"))
                .build();
        var otherUserSaved = userRepository.save(otherUser);

        var account = com.astrocode.backend.domain.entities.BankAccount.builder()
                .user(otherUserSaved)
                .name("Outra Conta")
                .initialBalance(BigDecimal.ZERO)
                .currentBalance(BigDecimal.ZERO)
                .type(com.astrocode.backend.domain.model.enums.AccountType.CHECKING)
                .build();
        account = bankAccountRepository.save(account);

        mockMvc.perform(delete("/api/accounts/" + account.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isForbidden());
    }
}
