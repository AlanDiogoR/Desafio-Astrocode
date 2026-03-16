package com.astrocode.backend.api.dto.creditcard;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PayBillRequest(
        @NotNull(message = "ID da conta bancária é obrigatório")
        UUID bankAccountId,

        @NotNull(message = "Data do pagamento é obrigatória")
        LocalDate payDate,

        @NotNull(message = "Valor é obrigatório")
        @Positive(message = "Valor deve ser maior que zero")
        BigDecimal amount
) {
}
