package com.astrocode.backend.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de resposta para informações do usuário.
 * Usa Record do Java 25 para imutabilidade e concisão.
 * NÃO inclui a senha por questões de segurança.
 */
public record UserResponse(
        UUID id,
        String name,
        String email,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
