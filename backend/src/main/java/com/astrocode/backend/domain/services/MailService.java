package com.astrocode.backend.domain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    private static final String SUBJECT = "Grivy - Código de recuperação de senha";
    private static final String BODY_TEMPLATE = """
            Olá!

            Seu código de recuperação de senha é: %s

            Este código expira em 15 minutos.

            Se você não solicitou esta alteração, ignore este e-mail.

            Grivy - Controle Financeiro""";

    private final RestClient restClient;
    private final String fromEmail;
    private final String brevoApiKey;

    public MailService(@Value("${brevo.api-key:}") String brevoApiKey,
                       @Value("${app.mail.from:grivycontrolefinanceiro@gmail.com}") String fromEmail) {
        this.brevoApiKey = brevoApiKey != null ? brevoApiKey.trim() : "";
        this.fromEmail = fromEmail;
        this.restClient = RestClient.builder()
                .baseUrl(BREVO_API_URL)
                .defaultHeader("api-key", this.brevoApiKey)
                .defaultHeader("accept", "application/json")
                .defaultHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public void sendPasswordResetCode(String toEmail, String code) {
        String body = String.format(BODY_TEMPLATE, code);

        if (brevoApiKey.isBlank()) {
            log.info("[DEV] Código de recuperação para {}: {}", toEmail, code);
            return;
        }

        sendViaBrevo(toEmail, code, body);
    }

    private void sendViaBrevo(String toEmail, String code, String body) {
        try {
            Map<String, Object> payload = Map.of(
                    "sender", Map.of("name", "Grivy", "email", fromEmail),
                    "to", List.of(Map.of("email", toEmail)),
                    "subject", SUBJECT,
                    "textContent", body
            );

            restClient.post()
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail via Brevo para {}: {}. Código (recupere nos logs): {}",
                    toEmail, e.getMessage(), code);
            log.warn("[FALLBACK] Brevo falhou. Código de recuperação para {}: {} (expira em 15 min)",
                    toEmail, code);
        }
    }
}
