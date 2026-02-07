package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.TransactionRequest;
import com.astrocode.backend.api.dto.TransactionResponse;
import com.astrocode.backend.api.dto.TransactionUpdateRequest;
import com.astrocode.backend.domain.entities.Transaction;
import com.astrocode.backend.domain.entities.User;
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

        var response = new TransactionResponse(
                transaction.getId(),
                transaction.getName(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getType().name(),
                transaction.getBankAccount().getId(),
                transaction.getCategory().getId(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );

        URI location = URI.create("/api/transactions/" + transaction.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) UUID bankAccountId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var transactions = transactionService.findAllByUserId(user.getId(), year, month, bankAccountId);

        List<TransactionResponse> response = transactions.stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getName(),
                        transaction.getAmount(),
                        transaction.getDate(),
                        transaction.getType().name(),
                        transaction.getBankAccount().getId(),
                        transaction.getCategory().getId(),
                        transaction.getCreatedAt(),
                        transaction.getUpdatedAt()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid TransactionUpdateRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var transaction = transactionService.update(id, request, user.getId());

        var response = new TransactionResponse(
                transaction.getId(),
                transaction.getName(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getType().name(),
                transaction.getBankAccount().getId(),
                transaction.getCategory().getId(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
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
