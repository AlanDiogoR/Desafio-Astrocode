package com.astrocode.backend.api.dto.subscription;

import jakarta.validation.constraints.NotBlank;

public record PreferenceCheckoutRequest(
        @NotBlank(message = "planId é obrigatório")
        String planId
) {
}
