package com.astrocode.backend.api.dto;

import java.math.BigDecimal;

public record DashboardResponse(
        BigDecimal totalBalance,
        BigDecimal totalIncomeMonth,
        BigDecimal totalExpenseMonth
) {
}
