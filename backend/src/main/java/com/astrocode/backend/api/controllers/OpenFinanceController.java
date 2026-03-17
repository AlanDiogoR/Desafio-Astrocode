package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.account.BankAccountResponse;
import com.astrocode.backend.domain.entities.BankAccount;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.services.OpenFinanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Open Finance", description = "Integração Pluggy - conectar bancos via Open Finance")
@RestController
@RequestMapping("/api/open-finance")
@SecurityRequirement(name = "bearer-jwt")
public class OpenFinanceController {

    private final OpenFinanceService openFinanceService;

    public OpenFinanceController(OpenFinanceService openFinanceService) {
        this.openFinanceService = openFinanceService;
    }

    @Operation(summary = "Connect Token", description = "Gera token para abrir o Pluggy Connect (plano Elite)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token criado"),
            @ApiResponse(responseCode = "402", description = "Plano Elite necessário"),
            @ApiResponse(responseCode = "503", description = "Open Finance não configurado")
    })
    @GetMapping("/connect-token")
    public ResponseEntity<Map<String, String>> getConnectToken(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        String token = openFinanceService.createConnectToken(user.getId());
        return ResponseEntity.ok(Map.of("accessToken", token));
    }

    @Operation(summary = "Sincronizar contas", description = "Importa contas do item Pluggy para o Grivy (plano Elite)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contas importadas"),
            @ApiResponse(responseCode = "402", description = "Plano Elite necessário")
    })
    @PostMapping("/sync")
    public ResponseEntity<List<BankAccountResponse>> syncAccounts(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body
    ) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        String itemId = body.get("itemId");
        if (itemId == null || itemId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        List<BankAccount> accounts = openFinanceService.syncAccountsFromPluggy(user.getId(), itemId);
        List<BankAccountResponse> response = accounts.stream()
                .map(a -> new BankAccountResponse(
                        a.getId(),
                        a.getName(),
                        a.getCurrentBalance(),
                        a.getType().name(),
                        a.getColor(),
                        a.getCreatedAt(),
                        a.getUpdatedAt()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Status", description = "Verifica se Open Finance está configurado")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> status() {
        return ResponseEntity.ok(Map.of("configured", openFinanceService.isConfigured()));
    }
}
