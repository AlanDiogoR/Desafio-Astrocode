package com.astrocode.backend.api.dto.subscription;

import com.astrocode.backend.domain.model.enums.PlanType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull(message = "Tipo de plano é obrigatório")
        PlanType planType,

        @NotBlank(message = "Token do cartão é obrigatório")
        String token,

        @NotBlank(message = "Método de pagamento é obrigatório")
        String paymentMethodId,

        @NotNull(message = "Parcelas é obrigatório")
        @Min(value = 1, message = "Parcelas deve ser pelo menos 1")
        Integer installments,

        @NotBlank(message = "E-mail do pagador é obrigatório")
        @Email(message = "E-mail inválido")
        String payerEmail,

        String payerIdentificationType,

        String payerIdentificationNumber,

        String issuerId
) {
}
