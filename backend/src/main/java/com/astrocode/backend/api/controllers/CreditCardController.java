package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.creditcard.CreditCardBillResponse;
import com.astrocode.backend.api.dto.creditcard.CreditCardRequest;
import com.astrocode.backend.api.dto.creditcard.CreditCardResponse;
import com.astrocode.backend.api.dto.creditcard.PayBillRequest;
import com.astrocode.backend.domain.entities.CreditCard;
import com.astrocode.backend.domain.entities.CreditCardBill;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.model.enums.BillStatus;
import com.astrocode.backend.domain.services.CreditCardService;
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

@Tag(name = "Cartões de Crédito", description = "CRUD de cartões de crédito e gestão de faturas")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/credit-cards")
public class CreditCardController {

    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @Operation(summary = "Criar cartão de crédito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cartão criado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Nome duplicado")
    })
    @PostMapping
    public ResponseEntity<CreditCardResponse> create(
            @RequestBody @Valid CreditCardRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var creditCard = creditCardService.create(request, user.getId());
        var response = toResponse(creditCard);
        URI location = URI.create("/api/credit-cards/" + creditCard.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Listar cartões do usuário")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de cartões")})
    @GetMapping
    public ResponseEntity<List<CreditCardResponse>> list(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        var creditCards = creditCardService.findAllByUserId(user.getId());
        var response = creditCards.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar cartão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão atualizado"),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado"),
            @ApiResponse(responseCode = "409", description = "Nome duplicado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CreditCardResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid CreditCardRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var creditCard = creditCardService.update(id, request, user.getId());
        return ResponseEntity.ok(toResponse(creditCard));
    }

    @Operation(summary = "Excluir cartão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cartão excluído"),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cartão possui despesas na fatura em aberto")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        creditCardService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obter fatura atual (OPEN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fatura em aberto"),
            @ApiResponse(responseCode = "404", description = "Fatura não encontrada")
    })
    @GetMapping("/{id}/bill/current")
    public ResponseEntity<CreditCardBillResponse> getCurrentBill(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var bill = creditCardService.getCurrentBill(id, user.getId());
        return ResponseEntity.ok(toBillResponse(bill));
    }

    @Operation(summary = "Histórico de faturas")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de faturas")})
    @GetMapping("/{id}/bills")
    public ResponseEntity<List<CreditCardBillResponse>> getBillHistory(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var bills = creditCardService.getBillHistory(id, user.getId());
        var response = bills.stream().map(this::toBillResponse).toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Pagar fatura")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fatura paga ou pagamento parcial registrado"),
            @ApiResponse(responseCode = "404", description = "Fatura ou conta não encontrada"),
            @ApiResponse(responseCode = "422", description = "Saldo insuficiente"),
            @ApiResponse(responseCode = "400", description = "Fatura já paga")
    })
    @PostMapping("/bills/{billId}/pay")
    public ResponseEntity<CreditCardBillResponse> payBill(
            @PathVariable UUID billId,
            @RequestBody @Valid PayBillRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        var updatedBill = creditCardService.payBill(billId, request, user.getId());
        return ResponseEntity.ok(toBillResponse(updatedBill));
    }

    private CreditCardResponse toResponse(CreditCard cc) {
        return new CreditCardResponse(
                cc.getId(),
                cc.getName(),
                cc.getCreditLimit(),
                cc.getClosingDay(),
                cc.getDueDay(),
                cc.getColor(),
                cc.getCurrentBillAmount(),
                cc.getCreatedAt()
        );
    }

    private CreditCardBillResponse toBillResponse(CreditCardBill bill) {
        return new CreditCardBillResponse(
                bill.getId(),
                bill.getCreditCard().getId(),
                bill.getMonth(),
                bill.getYear(),
                bill.getTotalAmount(),
                bill.getStatus().name(),
                bill.getDueDate(),
                bill.getClosingDate(),
                bill.getPaidDate(),
                bill.getCreatedAt()
        );
    }
}
