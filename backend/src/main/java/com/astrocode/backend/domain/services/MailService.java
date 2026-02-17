package com.astrocode.backend.domain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final boolean enabled;

    public MailService(JavaMailSender mailSender,
                       @Value("${app.mail.from:no-reply@grivy.app}") String fromEmail,
                       @Value("${spring.mail.host:}") String mailHost) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.enabled = mailHost != null && !mailHost.isBlank();
    }

    public void sendPasswordResetCode(String toEmail, String code) {
        if (!enabled) {
            log.info("[DEV] Código de recuperação para {}: {}", toEmail, code);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Grivy - Código de recuperação de senha");
            message.setText(String.format(
                    "Olá!\n\nSeu código de recuperação de senha é: %s\n\n" +
                    "Este código expira em 15 minutos.\n\n" +
                    "Se você não solicitou esta alteração, ignore este e-mail.\n\n" +
                    "Grivy - Controle Financeiro",
                    code
            ));
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de recuperação para {}: {}. Código (recupere nos logs): {}",
                    toEmail, e.getMessage(), code);
            log.warn("[FALLBACK] SMTP falhou. Código de recuperação para {}: {} (expira em 15 min)",
                    toEmail, code);
            // Não relançar: retorna 200, código salvo no DB e nos logs para recuperação
        }
    }
}
