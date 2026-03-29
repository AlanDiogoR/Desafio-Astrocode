package com.astrocode.backend.api.dto.subscription;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PayerRequest(
        @NotBlank(message = "e-mail do pagador é obrigatório")
        @Email(message = "e-mail inválido")
        String email,
        String firstName,
        String lastName,
        IdentificationRequest identification
) {
}
