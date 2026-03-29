package com.astrocode.backend.api.dto.subscription;

import java.time.LocalDateTime;

public record SubscriptionStatusApiResponse(
        String status,
        LocalDateTime expiresAt,
        boolean isActive
) {
}
