package com.astrocode.backend.api.dto.openfinance;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OpenFinanceWaitlistRequest(
        @NotBlank @Email @Size(max = 150) String email
) {
}
