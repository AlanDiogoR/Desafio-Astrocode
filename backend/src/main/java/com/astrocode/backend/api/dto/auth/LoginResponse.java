package com.astrocode.backend.api.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        UUID id,
        String name,
        String email,
        String plan,
        boolean isPro,
        boolean isElite,
        OffsetDateTime planExpiresAt
) {
}
