package com.astrocode.backend.infrastructure.whatsapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

/**
 * Valida o cabeçalho {@code X-Hub-Signature-256} do webhook Meta (HMAC-SHA256 do corpo bruto).
 */
@Component
public class MetaWebhookSignatureVerifier {

    private final String appSecret;

    public MetaWebhookSignatureVerifier(@Value("${whatsapp.app-secret:}") String appSecret) {
        this.appSecret = appSecret != null ? appSecret.trim() : "";
    }

    /**
     * @param rawBody corpo exatamente como recebido
     * @param signatureHeader valor de X-Hub-Signature-256 (formato {@code sha256=hex})
     * @return true se assinatura válida ou se o segredo não estiver configurado (modo dev)
     */
    public boolean isValid(String rawBody, String signatureHeader) {
        if (appSecret.isBlank()) {
            return true;
        }
        if (signatureHeader == null || !signatureHeader.startsWith("sha256=")) {
            return false;
        }
        try {
            String expectedHex = signatureHeader.substring("sha256=".length());
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));
            String actual = HexFormat.of().formatHex(hash);
            return constantTimeEquals(expectedHex.toLowerCase(), actual.toLowerCase());
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int r = 0;
        for (int i = 0; i < a.length(); i++) {
            r |= a.charAt(i) ^ b.charAt(i);
        }
        return r == 0;
    }
}
