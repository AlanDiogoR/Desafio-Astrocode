package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.account.BankAccountRequest;
import com.astrocode.backend.api.dto.account.BankAccountResponse;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.services.BankAccountService;
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

@Tag(name = "Contas bancárias", description = "CRUD de contas bancárias do usuário")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @Operation(summary = "Criar conta", description = "Cria nova conta bancária")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<BankAccountResponse> create(
            @RequestBody @Valid BankAccountRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var bankAccount = bankAccountService.create(request, user.getId());

        var response = new BankAccountResponse(
                bankAccount.getId(),
                bankAccount.getName(),
                bankAccount.getCurrentBalance(),
                bankAccount.getType().name(),
                bankAccount.getColor(),
                bankAccount.getCreatedAt(),
                bankAccount.getUpdatedAt()
        );

        URI location = URI.create("/api/accounts/" + bankAccount.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Listar contas", description = "Lista todas as contas bancárias do usuário")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de contas")})
    @GetMapping
    public ResponseEntity<List<BankAccountResponse>> getAll(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        var bankAccounts = bankAccountService.findAllByUserId(user.getId());

        List<BankAccountResponse> response = bankAccounts.stream()
                .map(account -> new BankAccountResponse(
                        account.getId(),
                        account.getName(),
                        account.getCurrentBalance(),
                        account.getType().name(),
                        account.getColor(),
                        account.getCreatedAt(),
                        account.getUpdatedAt()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar conta", description = "Atualiza conta bancária existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BankAccountResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid BankAccountRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var bankAccount = bankAccountService.update(id, request, user);

        var response = new BankAccountResponse(
                bankAccount.getId(),
                bankAccount.getName(),
                bankAccount.getCurrentBalance(),
                bankAccount.getType().name(),
                bankAccount.getColor(),
                bankAccount.getCreatedAt(),
                bankAccount.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Excluir conta", description = "Exclui conta bancária")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conta excluída"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        bankAccountService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
