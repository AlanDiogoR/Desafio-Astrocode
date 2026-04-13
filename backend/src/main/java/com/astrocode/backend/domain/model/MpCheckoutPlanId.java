package com.astrocode.backend.domain.model;

import com.astrocode.backend.domain.model.enums.PlanType;

/**
 * IDs de plano usados no checkout por Preferência (redirect Mercado Pago).
 * Mapeiam para {@link PlanType} interno.
 */
public final class MpCheckoutPlanId {

    public static final String PRO_MONTHLY = "PRO_MONTHLY";
    public static final String PRO_SEMIANNUAL = "PRO_SEMIANNUAL";
    public static final String PRO_ANNUAL = "PRO_ANNUAL";

    private MpCheckoutPlanId() {
    }

    public static PlanType toPlanType(String planId) {
        if (planId == null || planId.isBlank()) {
            throw new IllegalArgumentException("planId é obrigatório");
        }
        return switch (planId.trim()) {
            case PRO_MONTHLY -> PlanType.MONTHLY;
            case PRO_SEMIANNUAL -> PlanType.SEMIANNUAL;
            case PRO_ANNUAL -> PlanType.ANNUAL;
            default -> throw new IllegalArgumentException("Plano inválido: " + planId);
        };
    }

    public static String fromPlanType(PlanType planType) {
        return switch (planType) {
            case MONTHLY -> PRO_MONTHLY;
            case SEMIANNUAL -> PRO_SEMIANNUAL;
            case ANNUAL -> PRO_ANNUAL;
            case FREE -> throw new IllegalArgumentException("FREE não tem ID de checkout");
        };
    }
}
