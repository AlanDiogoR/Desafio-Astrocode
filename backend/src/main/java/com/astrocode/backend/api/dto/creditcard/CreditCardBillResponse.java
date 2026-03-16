package com.astrocode.backend.api.dto.creditcard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CreditCardBillResponse(
        UUID id,
        UUID creditCardId,
        Integer month,
        Integer year,
        BigDecimal totalAmount,
        String status,
        LocalDate dueDate,
        LocalDate closingDate,
        LocalDate paidDate,
        OffsetDateTime createdAt
) {
}
