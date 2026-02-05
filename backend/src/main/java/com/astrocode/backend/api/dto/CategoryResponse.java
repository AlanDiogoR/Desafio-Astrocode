package com.astrocode.backend.api.dto;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String icon,
        String type
) {
}
