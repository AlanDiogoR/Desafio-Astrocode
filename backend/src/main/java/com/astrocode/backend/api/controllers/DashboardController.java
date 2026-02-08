package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.DashboardResponse;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        var dashboardData = dashboardService.getDashboardData(user.getId());
        return ResponseEntity.ok(dashboardData);
    }
}
