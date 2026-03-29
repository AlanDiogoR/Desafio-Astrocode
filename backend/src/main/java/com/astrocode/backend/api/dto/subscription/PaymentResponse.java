package com.astrocode.backend.api.dto.subscription;

public record PaymentResponse(
        Long paymentId,
        String status,
        String statusDetail,
        String message
) {
}
