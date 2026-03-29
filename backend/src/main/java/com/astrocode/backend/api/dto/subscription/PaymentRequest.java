package com.astrocode.backend.api.dto.subscription;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank(message = "token é obrigatório")
        String token,

        @NotNull(message = "transactionAmount é obrigatório")
        @DecimalMin(value = "0.01", message = "valor deve ser maior que zero")
        BigDecimal transactionAmount,

        @NotNull(message = "installments é obrigatório")
        @Min(value = 1, message = "parcelas mínimas: 1")
        Integer installments,

        @NotBlank(message = "paymentMethodId é obrigatório")
        String paymentMethodId,

        @NotBlank(message = "planId é obrigatório")
        String planId,

        @NotNull(message = "payer é obrigatório")
        @Valid
        PayerRequest payer,

        @JsonProperty(required = false)
        String issuerId
) {
}
