package com.astrocode.backend.api.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
        String token,
        UUID id,
        String name,
        String email,
        String plan,
        boolean isPro,
        boolean isElite,
        OffsetDateTime planExpiresAt
) {
    public LoginResponse withoutToken() {
        return new LoginResponse(null, id, name, email, plan, isPro, isElite, planExpiresAt);
    }
}
