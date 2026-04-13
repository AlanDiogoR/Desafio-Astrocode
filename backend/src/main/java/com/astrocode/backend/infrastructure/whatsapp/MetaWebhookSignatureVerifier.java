package com.astrocode.backend.infrastructure.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Valida o cabeçalho {@code X-Hub-Signature-256} do webhook Meta (HMAC-SHA256 do corpo bruto)
 * e decodifica signed_request para callbacks de exclusão de dados.
 */
@Component
public class MetaWebhookSignatureVerifier {

    private final String appSecret;
    private final ObjectMapper objectMapper;

    public MetaWebhookSignatureVerifier(@Value("${whatsapp.app-secret:}") String appSecret,
                                        ObjectMapper objectMapper) {
        this.appSecret = appSecret != null ? appSecret.trim() : "";
        this.objectMapper = objectMapper;
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

    /**
     * Decodifica e valida um signed_request enviado pela Meta no callback de exclusão de dados.
     */
    public MetaDeletionPayload decodeSignedRequest(String signedRequest) throws Exception {
        String[] parts = signedRequest.split("\\.", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid signed_request format");
        }

        String encodedSig = parts[0];
        String encodedPayload = parts[1];

        if (!appSecret.isBlank()) {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] expectedSig = mac.doFinal(encodedPayload.getBytes(StandardCharsets.UTF_8));
            byte[] receivedSig = Base64.getUrlDecoder().decode(encodedSig);
            if (!MessageDigest.isEqual(expectedSig, receivedSig)) {
                throw new SecurityException("Invalid signed_request signature");
            }
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
        return objectMapper.readValue(payloadJson, MetaDeletionPayload.class);
    }

    public record MetaDeletionPayload(
            @JsonProperty("user_id") String userId,
            @JsonProperty("algorithm") String algorithm,
            @JsonProperty("issued_at") long issuedAt
    ) {}

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
