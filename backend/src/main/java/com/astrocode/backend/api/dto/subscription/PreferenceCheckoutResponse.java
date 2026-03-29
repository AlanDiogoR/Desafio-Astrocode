package com.astrocode.backend.api.dto.subscription;

public record PreferenceCheckoutResponse(
        String checkoutUrl,
        String preferenceId
) {
}
