package com.astrocode.backend.api.dto.user;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String plan,
        boolean isPro,
        boolean isElite,
        OffsetDateTime planExpiresAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
