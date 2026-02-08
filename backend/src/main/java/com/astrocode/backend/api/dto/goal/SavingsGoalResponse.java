package com.astrocode.backend.api.dto.goal;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SavingsGoalResponse(
        UUID id,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        String color,
        BigDecimal progressPercentage,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
