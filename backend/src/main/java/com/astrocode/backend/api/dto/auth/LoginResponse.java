package com.astrocode.backend.api.dto.auth;

public record LoginResponse(
        String token,
        String name
) {
}
