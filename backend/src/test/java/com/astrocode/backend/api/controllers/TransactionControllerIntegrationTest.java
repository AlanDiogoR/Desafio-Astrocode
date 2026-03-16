package com.astrocode.backend.api.controllers;

import com.astrocode.backend.domain.entities.BankAccount;
import com.astrocode.backend.domain.entities.Category;
import com.astrocode.backend.domain.entities.Transaction;
import com.astrocode.backend.domain.entities.User;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import com.astrocode.backend.domain.model.enums.AccountType;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.repositories.*;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;
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
@DisplayName("TransactionController - Integração")
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User savedUser;
    private BankAccount savedAccount;
    private Category savedCategory;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        var user = User.builder()
                .name("Transaction Test User")
                .email("tx.controller@" + UUID.randomUUID() + ".com")
                .password(passwordEncoder.encode("senha123"))
                .build();
        savedUser = userRepository.save(user);

        savedAccount = BankAccount.builder()
                .user(savedUser)
                .name("Conta Teste")
                .initialBalance(BigDecimal.valueOf(1000))
                .currentBalance(BigDecimal.valueOf(1000))
                .type(AccountType.CHECKING)
                .build();
        savedAccount = bankAccountRepository.save(savedAccount);

        savedCategory = Category.builder()
                .user(savedUser)
                .name("Moradia")
                .type(TransactionType.EXPENSE)
                .build();
        savedCategory = categoryRepository.save(savedCategory);

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

        String setCookie = result.getResponse().getHeader("Set-Cookie");
        if (setCookie != null && setCookie.contains("auth_token=")) {
            String cookiePart = setCookie.split(";")[0];
            return cookiePart.substring("auth_token=".length());
        }
        throw new IllegalStateException("Token não encontrado no Set-Cookie");
    }

    @Test
    @DisplayName("GET /api/transactions → 200 com paginação")
    void getTransactions_returns200WithPagination() throws Exception {
        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + authToken)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    @DisplayName("GET /api/transactions?size=101 → 400 Bad Request")
    void getTransactions_size101_returns400() throws Exception {
        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + authToken)
                        .param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/transactions?page=-1 → 400 Bad Request")
    void getTransactions_negativePage_returns400() throws Exception {
        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + authToken)
                        .param("page", "-1")
                        .param("size", "20"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/transactions?type=INVALIDO → 400 Bad Request")
    void getTransactions_invalidType_returns400() throws Exception {
        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + authToken)
                        .param("type", "INVALIDO"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/transactions com conta de outro usuário → 403")
    void postTransactions_accountNotOwned_returns403() throws Exception {
        var otherUser = User.builder()
                .name("Other")
                .email("other@" + UUID.randomUUID() + ".com")
                .password(passwordEncoder.encode("x"))
                .build();
        otherUser = userRepository.save(otherUser);
        var otherAccount = BankAccount.builder()
                .user(otherUser)
                .name("Outra Conta")
                .initialBalance(BigDecimal.ZERO)
                .currentBalance(BigDecimal.ZERO)
                .type(AccountType.CHECKING)
                .build();
        otherAccount = bankAccountRepository.save(otherAccount);

        Map<String, Object> body = new HashMap<>();
        body.put("name", "Despesa");
        body.put("amount", 100);
        body.put("date", LocalDate.now().toString());
        body.put("type", "EXPENSE");
        body.put("bankAccountId", otherAccount.getId().toString());
        body.put("categoryId", savedCategory.getId().toString());

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/transactions/{id} de outro usuário → 403")
    void putTransactions_otherUserTransaction_returns403() throws Exception {
        var otherUser = User.builder()
                .name("Other")
                .email("other@" + UUID.randomUUID() + ".com")
                .password(passwordEncoder.encode("x"))
                .build();
        otherUser = userRepository.save(otherUser);
        var otherAccount = BankAccount.builder()
                .user(otherUser)
                .name("Outra")
                .initialBalance(BigDecimal.ZERO)
                .currentBalance(BigDecimal.ZERO)
                .type(AccountType.CHECKING)
                .build();
        otherAccount = bankAccountRepository.save(otherAccount);
        var otherCategory = Category.builder()
                .user(otherUser)
                .name("Outra Cat")
                .type(TransactionType.EXPENSE)
                .build();
        otherCategory = categoryRepository.save(otherCategory);
        var otherTx = Transaction.builder()
                .user(otherUser)
                .bankAccount(otherAccount)
                .category(otherCategory)
                .name("Despesa")
                .amount(BigDecimal.valueOf(50))
                .date(LocalDate.now())
                .type(TransactionType.EXPENSE)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        otherTx = transactionRepository.save(otherTx);

        Map<String, Object> body = new HashMap<>();
        body.put("amount", 75);

        mockMvc.perform(put("/api/transactions/" + otherTx.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }
}
