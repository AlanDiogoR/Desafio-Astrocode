package com.astrocode.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Preços de assinatura e faixas para inferência de plano (webhook Mercado Pago).
 * Sobrescreva via application.properties ou variáveis de ambiente.
 */
@Component
@ConfigurationProperties(prefix = "app.subscription.pricing")
public class SubscriptionPricingProperties {

    private BigDecimal monthly = new BigDecimal("19.90");
    private BigDecimal semiannual = new BigDecimal("49.90");
    private BigDecimal annual = new BigDecimal("179.90");
    private BigDecimal minAnnualThreshold = new BigDecimal("150");
    private BigDecimal minSemiannualThreshold = new BigDecimal("40");

    public BigDecimal getMonthly() {
        return monthly;
    }

    public void setMonthly(BigDecimal monthly) {
        this.monthly = monthly;
    }

    public BigDecimal getSemiannual() {
        return semiannual;
    }

    public void setSemiannual(BigDecimal semiannual) {
        this.semiannual = semiannual;
    }

    public BigDecimal getAnnual() {
        return annual;
    }

    public void setAnnual(BigDecimal annual) {
        this.annual = annual;
    }

    public BigDecimal getMinAnnualThreshold() {
        return minAnnualThreshold;
    }

    public void setMinAnnualThreshold(BigDecimal minAnnualThreshold) {
        this.minAnnualThreshold = minAnnualThreshold;
    }

    public BigDecimal getMinSemiannualThreshold() {
        return minSemiannualThreshold;
    }

    public void setMinSemiannualThreshold(BigDecimal minSemiannualThreshold) {
        this.minSemiannualThreshold = minSemiannualThreshold;
    }
}
