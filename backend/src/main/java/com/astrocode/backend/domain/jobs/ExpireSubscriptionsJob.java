package com.astrocode.backend.domain.jobs;

import com.astrocode.backend.domain.services.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Job que executa diariamente para expirar assinaturas vencidas.
 * Roda às 01:00 AM.
 */
@Component
public class ExpireSubscriptionsJob {

    private static final Logger log = LoggerFactory.getLogger(ExpireSubscriptionsJob.class);

    private final SubscriptionService subscriptionService;

    public ExpireSubscriptionsJob(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void expireSubscriptions() {
        log.info("Iniciando job de expiração de assinaturas");
        try {
            subscriptionService.expireSubscriptions();
            log.info("Job de expiração de assinaturas concluído");
        } catch (Exception e) {
            log.error("Erro no job de expiração de assinaturas", e);
        }
    }
}
