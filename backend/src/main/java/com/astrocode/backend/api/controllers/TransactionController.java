package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.transaction.MonthlySummaryResponse;
import com.astrocode.backend.api.dto.transaction.TransactionRequest;
import com.astrocode.backend.api.dto.transaction.TransactionResponse;
import com.astrocode.backend.api.dto.transaction.TransactionUpdateRequest;
import com.astrocode.backend.domain.entities.Transaction;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        transactionService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
