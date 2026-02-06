package com.astrocode.backend.api.dto;

import com.astrocode.backend.domain.model.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record BankAccountRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String name,

        @NotNull(message = "Saldo inicial é obrigatório")
        BigDecimal initialBalance,

        @NotNull(message = "Tipo da conta é obrigatório")
        AccountType type,

        @Size(max = 30, message = "Cor deve ter no máximo 30 caracteres")
        String color
) {
}
