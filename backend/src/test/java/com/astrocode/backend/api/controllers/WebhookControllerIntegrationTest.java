package com.astrocode.backend.api.controllers;

import com.astrocode.backend.domain.repositories.WebhookProcessedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "mp.webhook-secret=test-webhook-secret-key-for-signature"
})
@Transactional
@DisplayName("WebhookController - Segurança e idempotência")
class WebhookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebhookProcessedEventRepository webhookProcessedEventRepository;

    private Map<String, Object> validPayload;

    @BeforeEach
    void setUp() {
        webhookProcessedEventRepository.deleteAll();
        validPayload = Map.of(
                "type", "payment",
                "data", Map.of("id", 12345L)
        );
    }

    @Test
    @DisplayName("Rejeita webhook quando secret configurado e assinatura ausente")
    void rejectWhenSignatureMissing() throws Exception {
        mockMvc.perform(post("/api/webhooks/mercadopago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPayload)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Rejeita webhook quando x-request-id ausente")
    void rejectWhenRequestIdMissing() throws Exception {
        mockMvc.perform(post("/api/webhooks/mercadopago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPayload))
                        .header("x-signature", "ts=123,v1=abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Rejeita webhook quando assinatura inválida")
    void rejectWhenSignatureInvalid() throws Exception {
        mockMvc.perform(post("/api/webhooks/mercadopago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPayload))
                        .header("x-signature", "ts=9999999999,v1=0000000000000000000000000000000000000000000000000000000000000000")
                        .header("x-request-id", "req-123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Aceita payload type diferente de payment sem processar")
    void acceptNonPaymentType() throws Exception {
        Map<String, Object> payload = Map.of(
                "type", "subscription",
                "data", Map.of("id", "sub_123")
        );
        mockMvc.perform(post("/api/webhooks/mercadopago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Retorna 400 quando data ausente")
    void rejectWhenDataMissing() throws Exception {
        Map<String, Object> payload = Map.of("type", "payment");
        mockMvc.perform(post("/api/webhooks/mercadopago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }
}
