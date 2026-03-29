package com.astrocode.backend.api.dto.subscription;

import java.time.LocalDateTime;

public record SubscriptionStatusResponse(
        String plan,
        boolean isActive,
        LocalDateTime expiresAt
) {
}
