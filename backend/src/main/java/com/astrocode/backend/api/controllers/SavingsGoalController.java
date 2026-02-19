package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.goal.SavingsGoalContributeRequest;
import com.astrocode.backend.api.dto.goal.SavingsGoalRequest;
import com.astrocode.backend.api.dto.goal.SavingsGoalWithdrawRequest;
import com.astrocode.backend.api.dto.goal.SavingsGoalResponse;
import com.astrocode.backend.domain.entities.SavingsGoal;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.services.SavingsGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Metas de poupança", description = "CRUD de metas, contribuição e saque")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/goals")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    @Operation(summary = "Criar meta", description = "Cria nova meta de poupança")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
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

    @Operation(summary = "Listar metas", description = "Lista todas as metas de poupança do usuário")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de metas")})
    @GetMapping
    public ResponseEntity<List<SavingsGoalResponse>> getAll(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        var savingsGoals = savingsGoalService.findAllByUserId(user.getId());

        List<SavingsGoalResponse> response = savingsGoals.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar meta", description = "Atualiza meta de poupança existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meta atualizada"),
            @ApiResponse(responseCode = "404", description = "Meta não encontrada")
    })
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

    @Operation(summary = "Contribuir", description = "Transfere valor da conta bancária para a meta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contribuição realizada"),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente"),
            @ApiResponse(responseCode = "404", description = "Meta ou conta não encontrada")
    })
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

    @Operation(summary = "Sacar", description = "Transfere valor da meta de volta para a conta bancária")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saque realizado"),
            @ApiResponse(responseCode = "400", description = "Valor inválido ou superior ao saldo da meta"),
            @ApiResponse(responseCode = "404", description = "Meta ou conta não encontrada")
    })
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

    @Operation(summary = "Excluir meta", description = "Exclui meta de poupança")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Meta excluída"),
            @ApiResponse(responseCode = "404", description = "Meta não encontrada")
    })
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
