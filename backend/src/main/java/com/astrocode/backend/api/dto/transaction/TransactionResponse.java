package com.astrocode.backend.api.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String name,
        BigDecimal amount,
        LocalDate date,
        String type,
        UUID bankAccountId,
        UUID categoryId,
        Boolean isRecurring,
        String frequency,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
