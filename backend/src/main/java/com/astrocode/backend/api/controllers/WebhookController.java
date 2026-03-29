package com.astrocode.backend.api.controllers;

import com.astrocode.backend.domain.services.MercadoPagoWebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final MercadoPagoWebhookService mercadoPagoWebhookService;

    public WebhookController(MercadoPagoWebhookService mercadoPagoWebhookService) {
        this.mercadoPagoWebhookService = mercadoPagoWebhookService;
    }

    @PostMapping("/mercadopago")
    public ResponseEntity<Void> handleMercadoPago(@RequestBody Map<String, Object> payload,
                                                  @RequestHeader(value = "x-signature", required = false) String signature,
                                                  @RequestHeader(value = "x-request-id", required = false) String requestId) {
        return mercadoPagoWebhookService.processWebhook(payload, signature, requestId);
    }
}
