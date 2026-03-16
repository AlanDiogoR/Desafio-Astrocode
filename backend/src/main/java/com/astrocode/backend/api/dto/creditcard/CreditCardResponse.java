package com.astrocode.backend.api.dto.creditcard;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CreditCardResponse(
        UUID id,
        String name,
        BigDecimal creditLimit,
        Integer closingDay,
        Integer dueDay,
        String color,
        BigDecimal currentBillAmount,
        OffsetDateTime createdAt
) {
}
