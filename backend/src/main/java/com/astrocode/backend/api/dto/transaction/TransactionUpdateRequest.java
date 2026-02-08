package com.astrocode.backend.api.dto.transaction;

import com.astrocode.backend.domain.model.enums.TransactionType;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionUpdateRequest(
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        String name,

        @Positive(message = "Valor deve ser maior que zero")
        BigDecimal amount,

        LocalDate date,

        TransactionType type,

        UUID bankAccountId,

        UUID categoryId
) {
}
