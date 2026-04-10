package com.astrocode.backend.infrastructure.whatsapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Cliente mínimo para envio de mensagens de texto pela WhatsApp Cloud API (Meta).
 */
@Component
public class WhatsappGraphClient {

    private static final Logger log = LoggerFactory.getLogger(WhatsappGraphClient.class);

    private final RestClient restClient;
    private final String phoneNumberId;
    private final String accessToken;

    public WhatsappGraphClient(
            @Value("${whatsapp.access-token:}") String accessToken,
            @Value("${whatsapp.phone-number-id:}") String phoneNumberId) {
        this.accessToken = accessToken != null ? accessToken.trim() : "";
        this.phoneNumberId = phoneNumberId != null ? phoneNumberId.trim() : "";
        this.restClient = RestClient.builder()
                .baseUrl("https://graph.facebook.com/v21.0")
                .build();
    }

    public boolean isConfigured() {
        return !accessToken.isBlank() && !phoneNumberId.isBlank();
    }

    /**
     * Envia mensagem de texto. {@code to} deve ser apenas dígitos (ex.: 5511999999999).
     */
    public void sendTextMessage(String toDigits, String textBody) {
        if (!isConfigured()) {
            log.info("[WHATSAPP] Envio omitido (token/phone id não configurados). Destino mascarado.");
            return;
        }
        String path = "/" + phoneNumberId + "/messages";
        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "to", toDigits,
                "type", "text",
                "text", Map.of("body", textBody)
        );
        restClient.post()
                .uri(path)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}
