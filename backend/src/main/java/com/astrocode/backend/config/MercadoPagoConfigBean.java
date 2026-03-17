package com.astrocode.backend.config;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfigBean {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoConfigBean.class);

    @Value("${mp.access-token:}")
    private String accessToken;

    @PostConstruct
    public void init() {
        if (accessToken != null && !accessToken.isBlank()) {
            MercadoPagoConfig.setAccessToken(accessToken);
        } else {
            log.warn("[CONFIG] mp.access-token não configurado. Pagamentos não funcionarão.");
        }
    }

    @Bean
    public PaymentClient paymentClient() {
        return new PaymentClient();
    }
}
