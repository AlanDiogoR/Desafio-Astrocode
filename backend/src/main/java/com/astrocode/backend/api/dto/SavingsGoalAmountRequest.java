package com.astrocode.backend.api.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SavingsGoalAmountRequest(
        @NotNull(message = "Valor é obrigatório")
        BigDecimal amount
) {
}
