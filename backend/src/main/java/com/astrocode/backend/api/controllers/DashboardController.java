package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.dashboard.DashboardResponse;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "Resumo financeiro consolidado")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "Obter resumo", description = "Retorna saldo total, receitas e despesas do mês atual")
    @ApiResponse(responseCode = "200", description = "Dados do dashboard")
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        var dashboardData = dashboardService.getDashboardData(user.getId());
        return ResponseEntity.ok(dashboardData);
    }
}
