package com.astrocode.backend.api.dto.auth;

import java.util.UUID;

public record LoginResponse(
        String token,
        UUID id,
        String name,
        String email
) {
}
