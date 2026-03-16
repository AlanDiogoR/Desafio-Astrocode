package com.astrocode.backend.api.dto.subscription;

import com.astrocode.backend.domain.model.enums.PlanType;
import com.astrocode.backend.domain.model.enums.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CheckoutResponse(
        UUID subscriptionId,
        PlanType planType,
        SubscriptionStatus status,
        BigDecimal amountPaid,
        String mpPaymentId,
        OffsetDateTime expiresAt
) {
}
