package com.astrocode.backend.api.dto.subscription;

import com.astrocode.backend.domain.model.enums.PlanType;
import com.astrocode.backend.domain.model.enums.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SubscriptionResponse(
        UUID id,
        PlanType planType,
        SubscriptionStatus status,
        OffsetDateTime startsAt,
        OffsetDateTime expiresAt,
        BigDecimal amountPaid,
        OffsetDateTime createdAt
) {
}
