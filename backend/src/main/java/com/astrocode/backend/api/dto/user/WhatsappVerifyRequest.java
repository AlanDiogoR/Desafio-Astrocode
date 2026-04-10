package com.astrocode.backend.api.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WhatsappVerifyRequest(
        @NotBlank @Size(min = 4, max = 12) String code
) {
}
