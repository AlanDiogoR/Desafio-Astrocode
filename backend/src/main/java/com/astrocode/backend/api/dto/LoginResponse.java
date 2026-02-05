package com.astrocode.backend.api.dto;

public record LoginResponse(
        String token,
        String name
) {
}
