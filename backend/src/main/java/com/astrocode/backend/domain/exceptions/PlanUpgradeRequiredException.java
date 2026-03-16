package com.astrocode.backend.domain.exceptions;

public class PlanUpgradeRequiredException extends RuntimeException {

    private final String feature;

    public PlanUpgradeRequiredException(String feature) {
        super("Você atingiu o limite do plano gratuito. Faça upgrade para continuar.");
        this.feature = feature;
    }

    public PlanUpgradeRequiredException(String message, String feature) {
        super(message);
        this.feature = feature;
    }

    public String getFeature() {
        return feature;
    }
}
