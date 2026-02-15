package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.goal.SavingsGoalContributeRequest;
import com.astrocode.backend.api.dto.goal.SavingsGoalRequest;
import com.astrocode.backend.api.dto.goal.SavingsGoalWithdrawRequest;
import com.astrocode.backend.api.dto.goal.SavingsGoalResponse;
import com.astrocode.backend.domain.entities.SavingsGoal;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.services.SavingsGoalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    @PostMapping
    public ResponseEntity<SavingsGoalResponse> create(
            @RequestBody @Valid SavingsGoalRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var savingsGoal = savingsGoalService.create(request, user.getId());

        var response = toResponse(savingsGoal);
        URI location = URI.create("/api/goals/" + savingsGoal.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SavingsGoalResponse>> getAll(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        var savingsGoals = savingsGoalService.findAllByUserId(user.getId());

        List<SavingsGoalResponse> response = savingsGoals.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid SavingsGoalRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var savingsGoal = savingsGoalService.update(id, request, user);

        var response = toResponse(savingsGoal);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/contribute")
    public ResponseEntity<SavingsGoalResponse> contribute(
            @PathVariable UUID id,
            @RequestBody @Valid SavingsGoalContributeRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var savingsGoal = savingsGoalService.contribute(id, request, user);

        var response = toResponse(savingsGoal);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<SavingsGoalResponse> withdraw(
            @PathVariable UUID id,
            @RequestBody @Valid SavingsGoalWithdrawRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var savingsGoal = savingsGoalService.withdraw(id, request, user);

        var response = toResponse(savingsGoal);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        savingsGoalService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    private SavingsGoalResponse toResponse(SavingsGoal savingsGoal) {
        BigDecimal progressPercentage = calculateProgressPercentage(
                savingsGoal.getCurrentAmount(),
                savingsGoal.getTargetAmount()
        );

        return new SavingsGoalResponse(
                savingsGoal.getId(),
                savingsGoal.getName(),
                savingsGoal.getTargetAmount(),
                savingsGoal.getCurrentAmount(),
                savingsGoal.getColor(),
                progressPercentage,
                savingsGoal.getStatus().name(),
                savingsGoal.getEndDate(),
                savingsGoal.getCreatedAt(),
                savingsGoal.getUpdatedAt()
        );
    }

    private BigDecimal calculateProgressPercentage(BigDecimal currentAmount, BigDecimal targetAmount) {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return currentAmount
                .divide(targetAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
