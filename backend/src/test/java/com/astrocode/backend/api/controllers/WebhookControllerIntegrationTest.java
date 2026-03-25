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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
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

    private static final String WEBHOOK_SECRET = "test-webhook-secret-key-for-signature";

    private Map<String, Object> validPayload;

    private static String mercadoPagoSignature(Map<String, Object> payload, String requestId, String secret) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        Object idObj = data != null ? data.get("id") : null;
        String dataId = idObj != null ? idObj.toString() : "";
        long ts = Instant.now().getEpochSecond();
        String manifest = "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts + ";";
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] calculated = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
        String v1 = HexFormat.of().formatHex(calculated);
        return "ts=" + ts + ",v1=" + v1;
    }

    @BeforeEach
    void setUp() {
        webhookProcessedEventRepository.deleteAll();
        validPayload = Map.of(
                "type", "payment",
                "data", Map.of("id", 12345L)
        );
    }

    @Test
    @DisplayName("Rejeita webhook quando assinatura ausente")
    void rejectWhenSignatureMissing() throws Exception {
        mockMvc.perform(post("/api/webhooks/mercadopago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPayload))
                        .header("x-request-id", "req-1"))
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
        String reqId = "req-sub-1";
        mockMvc.perform(post("/api/webhooks/mercadopago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                        .header("x-signature", mercadoPagoSignature(payload, reqId, WEBHOOK_SECRET))
                        .header("x-request-id", reqId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Retorna 400 quando data ausente")
    void rejectWhenDataMissing() throws Exception {
        Map<String, Object> payload = Map.of("type", "payment");
        String reqId = "req-no-data";
        mockMvc.perform(post("/api/webhooks/mercadopago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                        .header("x-signature", mercadoPagoSignature(payload, reqId, WEBHOOK_SECRET))
                        .header("x-request-id", reqId))
                .andExpect(status().isBadRequest());
    }
}
