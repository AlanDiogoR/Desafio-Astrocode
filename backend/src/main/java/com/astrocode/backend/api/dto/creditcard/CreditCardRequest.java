package com.astrocode.backend.api.dto.creditcard;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreditCardRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String name,

        @NotNull(message = "Limite de crédito é obrigatório")
        @DecimalMin(value = "0.01", message = "Limite deve ser maior que zero")
        BigDecimal creditLimit,

        @NotNull(message = "Dia de fechamento é obrigatório")
        @Min(value = 1, message = "Dia de fechamento deve ser entre 1 e 28")
        @Max(value = 28, message = "Dia de fechamento deve ser entre 1 e 28")
        Integer closingDay,

        @NotNull(message = "Dia de vencimento é obrigatório")
        @Min(value = 1, message = "Dia de vencimento deve ser entre 1 e 28")
        @Max(value = 28, message = "Dia de vencimento deve ser entre 1 e 28")
        Integer dueDay,

        @Size(max = 30, message = "Cor deve ter no máximo 30 caracteres")
        String color
) {
}
