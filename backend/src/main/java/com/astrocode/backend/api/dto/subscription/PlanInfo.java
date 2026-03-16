package com.astrocode.backend.api.dto.subscription;

import com.astrocode.backend.domain.model.enums.PlanType;

import java.math.BigDecimal;

public record PlanInfo(
        PlanType id,
        String name,
        BigDecimal price,
        Integer months,
        String description
) {
}
