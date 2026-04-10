package com.astrocode.backend.domain.services;

import com.astrocode.backend.infrastructure.email.ResendEmailClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Envio de e-mail transacional via Resend (preferencial) ou Brevo (legado).
 * Nunca registre URLs completas, códigos ou tokens em logs.
 */
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    private static final String SUBJECT = "Grivy - Código de recuperação de senha";
    private static final String EMAIL_VERIFICATION_SUBJECT = "Grivy - Confirme seu e-mail";
    private static final String RECURRING_INSUFFICIENT_BALANCE_SUBJECT = "Grivy - Despesa recorrente não registrada";
    private static final String RECURRING_INSUFFICIENT_BALANCE_TEMPLATE = """
            Olá!

            Sua despesa recorrente "%s" no valor de %s (data prevista: %s) não pôde ser registrada porque não há saldo suficiente na conta vinculada.

            Por favor, adicione fundos à sua conta para que as próximas cobranças sejam processadas automaticamente.

            Grivy - Controle Financeiro""";

    private final RestClient brevoClient;
    private final ResendEmailClient resendEmailClient;
    private final String fromEmail;
    private final String brevoApiKey;

    public MailService(
            @Value("${brevo.api-key:}") String brevoApiKey,
            @Value("${app.mail.from:grivycontrolefinanceiro@gmail.com}") String fromEmail,
            ResendEmailClient resendEmailClient) {
        this.brevoApiKey = brevoApiKey != null ? brevoApiKey.trim() : "";
        this.fromEmail = fromEmail;
        this.resendEmailClient = resendEmailClient;
        this.brevoClient = RestClient.builder()
                .baseUrl(BREVO_API_URL)
                .defaultHeader("api-key", this.brevoApiKey)
                .defaultHeader("accept", "application/json")
                .defaultHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Envia e-mail de verificação. O corpo não deve ser logado em claro.
     */
    public void sendEmailVerification(String toEmail, String verificationUrl) {
        String body = String.format(
                """
                        Olá!

                        Confirme seu cadastro no Grivy acessando o link abaixo:

                        %s

                        Se você não criou uma conta, ignore este e-mail.

                        Grivy - Controle Financeiro""",
                verificationUrl);
        if (!hasMailProvider()) {
            log.info("[DEV] E-mail de verificação enfileirado para destinatário mascarado={} (configure RESEND_API_KEY ou BREVO_API_KEY)",
                    maskEmail(toEmail));
            return;
        }
        sendTransactional(toEmail, EMAIL_VERIFICATION_SUBJECT, null, body, e ->
                log.warn("[MAIL] Falha ao enviar verificação para {}.", maskEmail(toEmail)));
    }

    public void sendPasswordResetCode(String toEmail, String code) {
        String body = String.format("""
                Olá!

                Seu código de recuperação de senha é: %s

                Este código expira em 15 minutos.

                Se você não solicitou esta alteração, ignore este e-mail.

                Grivy - Controle Financeiro""", code);

        if (!hasMailProvider()) {
            log.info("[DEV] Código de recuperação gerado para destinatário mascarado={} (configure RESEND_API_KEY ou BREVO_API_KEY)",
                    maskEmail(toEmail));
            return;
        }
        sendTransactional(toEmail, SUBJECT, null, body, e ->
                log.warn("[MAIL] Falha ao enviar recuperação para {}.", maskEmail(toEmail)));
    }

    public void sendRecurringExpenseNotAddedDueToInsufficientBalance(String toEmail, String expenseName, String amountFormatted, String date) {
        String body = String.format(RECURRING_INSUFFICIENT_BALANCE_TEMPLATE, expenseName, amountFormatted, date);

        if (!hasMailProvider()) {
            log.info("[DEV] Aviso de saldo insuficiente (despesa recorrente) para {}", maskEmail(toEmail));
            return;
        }

        sendTransactional(toEmail, RECURRING_INSUFFICIENT_BALANCE_SUBJECT, null, body, null);
    }

    /**
     * Envio genérico HTML + texto (marketing/transacional).
     *
     * @param toEmail destinatário
     * @param subject assunto
     * @param htmlHtml corpo HTML
     * @param textPlain corpo texto plano
     */
    public void sendHtmlAndText(String toEmail, String subject, String htmlHtml, String textPlain) {
        if (!hasMailProvider()) {
            log.info("[DEV] E-mail transacional omitido (sem provedor) para {} assunto={}", maskEmail(toEmail), subject);
            return;
        }
        sendTransactional(toEmail, subject, htmlHtml, textPlain, e ->
                log.warn("[MAIL] Falha ao enviar para {} assunto={}", maskEmail(toEmail), subject));
    }

    private boolean hasMailProvider() {
        return resendEmailClient.isConfigured() || !brevoApiKey.isBlank();
    }

    private void sendTransactional(String toEmail, String subject, String html, String text, Consumer<Exception> onFailure) {
        try {
            if (resendEmailClient.isConfigured()) {
                resendEmailClient.sendWithRetry(fromEmail, toEmail, subject, html, text);
                return;
            }
            sendViaBrevo(toEmail, subject, text != null ? text : "", onFailure);
        } catch (Exception e) {
            log.error("[MAIL] Falha ao enviar para {}: {}", maskEmail(toEmail), e.getMessage());
            if (onFailure != null) {
                onFailure.accept(e);
            }
        }
    }

    private void sendViaBrevo(String toEmail, String subject, String body, Consumer<Exception> onFailure) {
        try {
            Map<String, Object> payload = Map.of(
                    "sender", Map.of("name", "Grivy", "email", fromEmail),
                    "to", List.of(Map.of("email", toEmail)),
                    "subject", subject,
                    "textContent", body
            );

            brevoClient.post()
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail via Brevo para {}: {}", maskEmail(toEmail), e.getMessage());
            if (onFailure != null) {
                onFailure.accept(e);
            }
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
