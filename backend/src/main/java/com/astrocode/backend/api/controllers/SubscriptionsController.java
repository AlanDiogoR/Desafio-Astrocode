package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.subscription.PaymentRequest;
import com.astrocode.backend.api.dto.subscription.PaymentResponse;
import com.astrocode.backend.api.dto.subscription.SubscriptionStatusResponse;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.services.MercadoPagoWebhookService;
import com.astrocode.backend.domain.services.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Assinaturas (Checkout MP)", description = "Pagamento transparente Mercado Pago e status")
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionsController {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionsController.class);

    private final SubscriptionService subscriptionService;
    private final MercadoPagoWebhookService mercadoPagoWebhookService;

    public SubscriptionsController(SubscriptionService subscriptionService,
                                   MercadoPagoWebhookService mercadoPagoWebhookService) {
        this.subscriptionService = subscriptionService;
        this.mercadoPagoWebhookService = mercadoPagoWebhookService;
    }

    @Operation(summary = "Processar pagamento (checkout transparente)", description = "Token do cartão gerado pelo MP.js no frontend")
    @SecurityRequirement(name = "bearer-jwt")
    @PostMapping("/process-payment")
    public ResponseEntity<PaymentResponse> processPayment(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid PaymentRequest request) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        log.info("[CHECKOUT] Processando pagamento para userId={} planId={}", user.getId(), request.planId());
        return ResponseEntity.ok(subscriptionService.processPayment(user.getId(), request));
    }

    @Operation(summary = "Status da assinatura (resumo)")
    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping("/status")
    public ResponseEntity<SubscriptionStatusResponse> getStatus(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(subscriptionService.getStatus(user.getId()));
    }

    @Operation(summary = "Webhook Mercado Pago", description = "Público — validação de assinatura (x-signature)")
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload,
                                        @RequestHeader(value = "x-signature", required = false) String signature,
                                        @RequestHeader(value = "x-request-id", required = false) String requestId) {
        return mercadoPagoWebhookService.processWebhook(payload, signature, requestId);
    }
}
