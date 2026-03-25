package com.astrocode.backend.domain.jobs;

import com.astrocode.backend.domain.services.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Expira assinaturas pagas vencidas (diário 01:00, fuso {@link #SCHEDULE_ZONE}). */
@Component
public class ExpireSubscriptionsJob {

    private static final Logger log = LoggerFactory.getLogger(ExpireSubscriptionsJob.class);

    private static final String SCHEDULE_ZONE = "America/Sao_Paulo";

    private final SubscriptionService subscriptionService;

    public ExpireSubscriptionsJob(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Scheduled(cron = "0 0 1 * * ?", zone = SCHEDULE_ZONE)
    @Transactional
    public void expireSubscriptions() {
        log.info("Iniciando job de expiração de assinaturas (zone={})", SCHEDULE_ZONE);
        subscriptionService.expireSubscriptions();
        log.info("Job de expiração de assinaturas concluído");
    }
}
