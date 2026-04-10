package com.astrocode.backend.infrastructure.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP para a API REST do Resend (envio transacional).
 * Documentação: https://resend.com/docs/api-reference/emails/send-email
 */
@Component
public class ResendEmailClient {

    private static final Logger log = LoggerFactory.getLogger(ResendEmailClient.class);
    private static final String RESEND_URL = "https://api.resend.com/emails";

    private final RestClient restClient;
    private final String apiKey;

    public ResendEmailClient(
            @org.springframework.beans.factory.annotation.Value("${resend.api-key:}") String apiKey) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.restClient = RestClient.builder()
                .baseUrl(RESEND_URL)
                .defaultHeader("Authorization", "Bearer " + this.apiKey)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public boolean isConfigured() {
        return !apiKey.isBlank();
    }

    /**
     * Envia um e-mail via Resend com até 3 tentativas e backoff exponencial.
     *
     * @param from remetente verificado no Resend
     * @param to destinatário
     * @param subject assunto
     * @param html corpo HTML (pode ser null)
     * @param text corpo texto plano
     */
    public void sendWithRetry(String from, String to, String subject, String html, String text) {
        Exception last = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                sendOnce(from, to, subject, html, text);
                return;
            } catch (Exception e) {
                last = e;
                log.warn("[RESEND] Tentativa {} falhou para destinatário mascarado={}: {}",
                        attempt, maskEmail(to), e.getMessage());
                if (attempt < 3) {
                    sleepBackoff(attempt);
                }
            }
        }
        throw new IllegalStateException("Resend: falha após 3 tentativas", last);
    }

    private void sendOnce(String from, String to, String subject, String html, String text) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("from", from);
        body.put("to", List.of(to));
        body.put("subject", subject);
        if (text != null && !text.isBlank()) {
            body.put("text", text);
        }
        if (html != null && !html.isBlank()) {
            body.put("html", html);
        }

        restClient.post()
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    private static void sleepBackoff(int attempt) {
        long ms = (long) Math.pow(2, attempt) * 200L;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        int at = email.indexOf('@');
        String local = email.substring(0, at);
        String domain = email.substring(at);
        String visible = local.length() <= 2 ? "*" : local.substring(0, 2) + "***";
        return visible + domain;
    }
}
