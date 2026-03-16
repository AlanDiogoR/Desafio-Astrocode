package com.astrocode.backend.api.dto.user;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String name,

        String currentPassword,

        @Size(min = 8, max = 255, message = "Senha deve ter no mínimo 8 caracteres")
        String newPassword
) {
}
