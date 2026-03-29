package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.subscription.PlanInfo;
import com.astrocode.backend.api.dto.subscription.SubscriptionResponse;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.services.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Assinaturas", description = "Planos e gestão de assinatura")
@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Operation(summary = "Listar planos", description = "Retorna os planos disponíveis com preços")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de planos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping("/plans")
    public ResponseEntity<List<PlanInfo>> listPlans() {
        return ResponseEntity.ok(subscriptionService.listPlans());
    }

    @Operation(summary = "Minha assinatura", description = "Retorna a assinatura do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assinatura retornada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Assinatura não encontrada")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping("/me")
    public ResponseEntity<SubscriptionResponse> getMe(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        var subscription = subscriptionService.getSubscriptionOrThrow(user.getId());
        return ResponseEntity.ok(subscriptionService.toResponse(subscription));
    }

    @Operation(summary = "Cancelar assinatura", description = "Cancela a assinatura paga do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Assinatura cancelada"),
            @ApiResponse(responseCode = "400", description = "Assinatura gratuita não pode ser cancelada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Assinatura não encontrada")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PostMapping("/cancel")
    public ResponseEntity<Void> cancel(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        subscriptionService.cancelSubscription(user.getId());
        return ResponseEntity.noContent().build();
    }
}
