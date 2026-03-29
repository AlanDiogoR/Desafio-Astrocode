package com.astrocode.backend.api.dto.subscription;

public record IdentificationRequest(
        String type,
        String number
) {
}
