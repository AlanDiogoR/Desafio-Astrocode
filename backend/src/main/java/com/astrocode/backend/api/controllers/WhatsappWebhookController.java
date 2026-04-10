package com.astrocode.backend.api.controllers;

import com.astrocode.backend.domain.services.WhatsappInboundService;
import com.astrocode.backend.infrastructure.whatsapp.MetaWebhookSignatureVerifier;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Webhook Meta WhatsApp Cloud API — verificação GET e eventos POST.
 */
@Hidden
@RestController
@RequestMapping("/api/webhooks/whatsapp")
public class WhatsappWebhookController {

    private final String verifyToken;
    private final MetaWebhookSignatureVerifier signatureVerifier;
    private final WhatsappInboundService inboundService;

    public WhatsappWebhookController(
            @Value("${whatsapp.verify-token:}") String verifyToken,
            MetaWebhookSignatureVerifier signatureVerifier,
            WhatsappInboundService inboundService) {
        this.verifyToken = verifyToken != null ? verifyToken.trim() : "";
        this.signatureVerifier = signatureVerifier;
        this.inboundService = inboundService;
    }

    @GetMapping
    public ResponseEntity<String> verify(
            @RequestParam(name = "hub.mode") String mode,
            @RequestParam(name = "hub.verify_token") String token,
            @RequestParam(name = "hub.challenge") String challenge) {
        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(403).body("Forbidden");
    }

    @PostMapping
    public ResponseEntity<String> receive(
            @RequestBody String rawBody,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature) {
        if (!signatureVerifier.isValid(rawBody, signature)) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        inboundService.processWebhookJson(rawBody);
        return ResponseEntity.ok("EVENT_RECEIVED");
    }
}
