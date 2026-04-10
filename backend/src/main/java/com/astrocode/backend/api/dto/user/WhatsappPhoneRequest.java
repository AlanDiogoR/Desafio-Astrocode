package com.astrocode.backend.api.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WhatsappPhoneRequest(
        @NotBlank @Size(max = 20) String phone
) {
}
