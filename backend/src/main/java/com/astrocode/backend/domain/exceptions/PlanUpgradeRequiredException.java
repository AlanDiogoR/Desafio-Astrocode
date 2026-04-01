package com.astrocode.backend.domain.exceptions;

public class PlanUpgradeRequiredException extends RuntimeException {

    private final String feature;

    public PlanUpgradeRequiredException(String feature) {
        super("Esse recurso faz parte do Grivy Pro ou Elite. Faça upgrade para desbloquear.");
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
