package com.astrocode.backend.api.dto.transaction;

import java.math.BigDecimal;
import java.util.List;

public record MonthlySummaryResponse(
        BigDecimal totalExpense,
        List<CategoryExpenseItem> byCategory
) {
}
