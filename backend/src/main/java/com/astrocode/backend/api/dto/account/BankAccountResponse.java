package com.astrocode.backend.api.dto.account;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BankAccountResponse(
        UUID id,
        String name,
        BigDecimal initialBalance,
        String type,
        String color,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
