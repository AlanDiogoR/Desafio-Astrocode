package com.astrocode.backend.infrastructure.whatsapp;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class MetaWebhookSignatureVerifierTest {

    @Test
    void validaAssinaturaHmac() throws Exception {
        String secret = "test-secret";
        String body = "{\"test\":true}";
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(body.getBytes(StandardCharsets.UTF_8));
        String sig = "sha256=" + HexFormat.of().formatHex(hash);

        MetaWebhookSignatureVerifier v = new MetaWebhookSignatureVerifier(secret);
        assertTrue(v.isValid(body, sig));
    }

    @Test
    void rejeitaAssinaturaErrada() {
        MetaWebhookSignatureVerifier v = new MetaWebhookSignatureVerifier("secret");
        assertFalse(v.isValid("{}", "sha256=deadbeef"));
    }
}
