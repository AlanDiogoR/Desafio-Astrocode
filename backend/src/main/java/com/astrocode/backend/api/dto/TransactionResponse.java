package com.astrocode.backend.api.dto;

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
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
