package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.subscription.PreferenceCheckoutRequest;
import com.astrocode.backend.api.dto.subscription.PreferenceCheckoutResponse;
import com.astrocode.backend.api.dto.subscription.SubscriptionStatusApiResponse;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.services.MercadoPagoWebhookService;
import com.astrocode.backend.domain.services.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Assinaturas (Checkout MP)", description = "Preferência Mercado Pago (redirect) e status")
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionsController {

    private final SubscriptionService subscriptionService;
    private final MercadoPagoWebhookService mercadoPagoWebhookService;

    public SubscriptionsController(SubscriptionService subscriptionService,
                                   MercadoPagoWebhookService mercadoPagoWebhookService) {
        this.subscriptionService = subscriptionService;
        this.mercadoPagoWebhookService = mercadoPagoWebhookService;
    }

    @Operation(summary = "Criar checkout (Preferência MP)", description = "Retorna URL do Mercado Pago para pagamento")
    @SecurityRequirement(name = "bearer-jwt")
    @PostMapping("/checkout")
    public ResponseEntity<PreferenceCheckoutResponse> createCheckout(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid PreferenceCheckoutRequest request) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(subscriptionService.createPreferenceCheckout(user.getId(), request.planId()));
    }

    @Operation(summary = "Webhook Mercado Pago", description = "Público — mesma validação de assinatura que /api/webhooks/mercadopago")
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload,
                                        @RequestHeader(value = "x-signature", required = false) String signature,
                                        @RequestHeader(value = "x-request-id", required = false) String requestId) {
        return mercadoPagoWebhookService.processWebhook(payload, signature, requestId);
    }

    @Operation(summary = "Status da assinatura (resumo)")
    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping("/status")
    public ResponseEntity<SubscriptionStatusApiResponse> getStatus(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(subscriptionService.getSubscriptionStatusForApi(user.getId()));
    }
}
