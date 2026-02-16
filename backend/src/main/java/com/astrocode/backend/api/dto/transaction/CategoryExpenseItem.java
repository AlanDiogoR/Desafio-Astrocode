package com.astrocode.backend.api.dto.transaction;

import java.math.BigDecimal;
import java.util.UUID;

public record CategoryExpenseItem(
        UUID categoryId,
        String categoryName,
        BigDecimal totalAmount
) {
}
