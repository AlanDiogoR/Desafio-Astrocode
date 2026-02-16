package com.astrocode.backend.api.dto.transaction;

import com.astrocode.backend.domain.model.enums.RecurrenceFrequency;
import com.astrocode.backend.domain.model.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        String name,

        @NotNull(message = "Valor é obrigatório")
        @Positive(message = "Valor deve ser maior que zero")
        BigDecimal amount,

        @NotNull(message = "Data é obrigatória")
        LocalDate date,

        @NotNull(message = "Tipo da transação é obrigatório")
        TransactionType type,

        @NotNull(message = "ID da conta bancária é obrigatório")
        UUID bankAccountId,

        @NotNull(message = "ID da categoria é obrigatório")
        UUID categoryId,

        Boolean isRecurring,

        RecurrenceFrequency frequency
) {
}
