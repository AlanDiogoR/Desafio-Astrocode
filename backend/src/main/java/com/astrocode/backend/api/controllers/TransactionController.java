package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.transaction.MonthlySummaryResponse;
import com.astrocode.backend.api.dto.transaction.TransactionRequest;
import com.astrocode.backend.api.dto.transaction.TransactionResponse;
import com.astrocode.backend.api.dto.transaction.TransactionUpdateRequest;
import com.astrocode.backend.domain.entities.Transaction;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Transações", description = "CRUD de transações financeiras com filtros")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Criar transação", description = "Cria nova transação e atualiza saldo da conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente")
    })
    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @RequestBody @Valid TransactionRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var transaction = transactionService.create(request, user.getId());

        var response = toResponse(transaction);

        URI location = URI.create("/api/transactions/" + transaction.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Listar transações", description = "Lista transações com filtros opcionais (year, month, bankAccountId, type)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de transações")})
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) UUID bankAccountId,
            @RequestParam(required = false) TransactionType type,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var transactions = transactionService.findAllByUserId(user.getId(), year, month, bankAccountId, type);

        List<TransactionResponse> response = transactions.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Resumo mensal", description = "Retorna resumo de gastos por categoria no período")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Resumo mensal")})
    @GetMapping("/analytics/monthly-summary")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var summary = transactionService.getMonthlySummary(user.getId(), year, month);
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "Atualizar transação", description = "Atualiza transação existente e reconcilia saldo da conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transação atualizada"),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid TransactionUpdateRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var transaction = transactionService.update(id, request, user.getId());

        return ResponseEntity.ok(toResponse(transaction));
    }

    @Operation(summary = "Excluir transação", description = "Exclui transação e reverte saldo da conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transação excluída"),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        transactionService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getName(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getType().name(),
                transaction.getBankAccount().getId(),
                transaction.getCategory().getId(),
                transaction.getIsRecurring() != null ? transaction.getIsRecurring() : false,
                transaction.getFrequency() != null ? transaction.getFrequency().name() : null,
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
