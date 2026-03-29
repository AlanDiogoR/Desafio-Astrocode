package com.astrocode.backend.api.dto.user;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(
        @NotBlank(message = "Senha é obrigatória para excluir a conta")
        String password
) {
}
