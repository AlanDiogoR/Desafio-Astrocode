package com.astrocode.backend.api.controllers;

import com.astrocode.backend.domain.entities.WebhookProcessedEvent;
import com.astrocode.backend.domain.repositories.WebhookProcessedEventRepository;
import com.astrocode.backend.domain.services.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final SubscriptionService subscriptionService;
    private final WebhookProcessedEventRepository webhookProcessedEventRepository;

    @Value("${mp.webhook-secret:}")
    private String webhookSecret;

    public WebhookController(SubscriptionService subscriptionService,
                              WebhookProcessedEventRepository webhookProcessedEventRepository) {
        this.subscriptionService = subscriptionService;
        this.webhookProcessedEventRepository = webhookProcessedEventRepository;
    }

    @PostMapping("/mercadopago")
    public ResponseEntity<Void> handleMercadoPago(@RequestBody Map<String, Object> payload,
                                                  @RequestHeader(value = "x-signature", required = false) String signature,
                                                  @RequestHeader(value = "x-request-id", required = false) String requestId) {
        try {
            if (webhookSecret == null || webhookSecret.isBlank()) {
                log.error("CRITICO: MP_WEBHOOK_SECRET não configurado. Webhooks rejeitados em todos os ambientes.");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }

            if (signature == null || signature.isBlank()) {
                log.warn("Webhook Mercado Pago: assinatura obrigatória (x-signature)");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (requestId == null || requestId.isBlank()) {
                log.warn("Webhook Mercado Pago: x-request-id obrigatório para validação");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (!validateSignature(payload, signature, requestId)) {
                log.warn("Webhook Mercado Pago: assinatura inválida");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String type = (String) payload.get("type");
            if (!"payment".equals(type)) {
                return ResponseEntity.ok().build();
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            if (data == null) {
                return ResponseEntity.badRequest().build();
            }

            Object idObj = data.get("id");
            if (idObj == null) {
                return ResponseEntity.badRequest().build();
            }

            Long paymentId = idObj instanceof Number ? ((Number) idObj).longValue() : Long.parseLong(idObj.toString());
            String effectiveRequestId = (requestId != null && !requestId.isBlank()) ? requestId : "legacy-" + paymentId;

            if (webhookProcessedEventRepository.existsByPaymentIdAndRequestId(paymentId, effectiveRequestId)) {
                log.debug("Webhook Mercado Pago: evento já processado (idempotência) paymentId={} requestId={}", paymentId, effectiveRequestId);
                return ResponseEntity.ok().build();
            }

            webhookProcessedEventRepository.save(new WebhookProcessedEvent(paymentId, effectiveRequestId));
            subscriptionService.handlePaymentApproved(paymentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro ao processar webhook Mercado Pago", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private static final long REPLAY_WINDOW_SECONDS = 300;

    private boolean validateSignature(Map<String, Object> payload, String xSignature, String requestId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            Object idObj = data != null ? data.get("id") : null;
            String dataId = idObj != null ? idObj.toString() : "";

            String ts = null;
            String v1 = null;
            for (String part : xSignature.split(",")) {
                String[] kv = part.split("=", 2);
                if (kv.length == 2) {
                    if ("ts".equals(kv[0].trim())) ts = kv[1].trim();
                    if ("v1".equals(kv[0].trim())) v1 = kv[1].trim();
                }
            }
            if (ts == null || v1 == null || requestId == null) {
                return false;
            }

            long signatureTs;
            try {
                signatureTs = Long.parseLong(ts);
            } catch (NumberFormatException e) {
                return false;
            }
            long now = Instant.now().getEpochSecond();
            if (Math.abs(now - signatureTs) > REPLAY_WINDOW_SECONDS) {
                log.warn("Webhook Mercado Pago: timestamp fora da janela anti-replay (ts={} now={})", ts, now);
                return false;
            }

            String manifest = "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts + ";";
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] calculated = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
            byte[] received = HexFormat.of().parseHex(v1);
            return java.security.MessageDigest.isEqual(calculated, received);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalArgumentException e) {
            log.error("Erro na validação da assinatura", e);
            return false;
        }
    }
}
