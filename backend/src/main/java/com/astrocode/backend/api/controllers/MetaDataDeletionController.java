package com.astrocode.backend.api.controllers;

import com.astrocode.backend.domain.services.MetaDataDeletionService;
import com.astrocode.backend.infrastructure.whatsapp.MetaWebhookSignatureVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/webhooks/meta/data-deletion")
public class MetaDataDeletionController {

    private static final Logger log = LoggerFactory.getLogger(MetaDataDeletionController.class);

    private final MetaDataDeletionService deletionService;
    private final MetaWebhookSignatureVerifier signatureVerifier;

    public MetaDataDeletionController(MetaDataDeletionService deletionService,
                                      MetaWebhookSignatureVerifier signatureVerifier) {
        this.deletionService = deletionService;
        this.signatureVerifier = signatureVerifier;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> handleDeletionRequest(
            @RequestBody Map<String, String> body) {

        String signedRequest = body.get("signed_request");
        if (signedRequest == null || signedRequest.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            MetaWebhookSignatureVerifier.MetaDeletionPayload payload =
                    signatureVerifier.decodeSignedRequest(signedRequest);

            String confirmationCode = deletionService.scheduleUserDataDeletion(payload.userId());

            log.info("Data deletion scheduled for Meta user, confirmation: {}", confirmationCode);

            Map<String, String> response = new HashMap<>();
            response.put("url", "https://grivy.netlify.app/exclusao-de-dados?code=" + confirmationCode);
            response.put("confirmation_code", confirmationCode);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to process Meta data deletion request", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/status/{code}")
    public ResponseEntity<Map<String, String>> getStatus(@PathVariable String code) {
        return deletionService.getDeletionStatus(code)
                .map(status -> ResponseEntity.ok(Map.of("status", status, "code", code)))
                .orElse(ResponseEntity.notFound().build());
    }
}
