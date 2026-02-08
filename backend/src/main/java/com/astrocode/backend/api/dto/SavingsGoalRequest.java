package com.astrocode.backend.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record SavingsGoalRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
        String name,

        @NotNull(message = "Valor alvo é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor alvo deve ser maior que zero")
        BigDecimal targetAmount,

        @Size(max = 30, message = "Cor deve ter no máximo 30 caracteres")
        String color
) {
}
