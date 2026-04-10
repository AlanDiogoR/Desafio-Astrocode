package com.astrocode.backend.domain.jobs;

import com.astrocode.backend.domain.entities.ScheduledMarketingEmail;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.repositories.ScheduledMarketingEmailRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import com.astrocode.backend.domain.services.EmailMarketingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Processa e-mails de marketing agendados e campanha de reativação.
 */
@Component
public class GrivyScheduledJobs {

    private static final Logger log = LoggerFactory.getLogger(GrivyScheduledJobs.class);

    private final ScheduledMarketingEmailRepository scheduledMarketingEmailRepository;
    private final EmailMarketingService emailMarketingService;
    private final UserRepository userRepository;

    public GrivyScheduledJobs(
            ScheduledMarketingEmailRepository scheduledMarketingEmailRepository,
            EmailMarketingService emailMarketingService,
            UserRepository userRepository) {
        this.scheduledMarketingEmailRepository = scheduledMarketingEmailRepository;
        this.emailMarketingService = emailMarketingService;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void processMarketingQueue() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<ScheduledMarketingEmail> due = scheduledMarketingEmailRepository.findDue(now);
        for (ScheduledMarketingEmail job : due) {
            try {
                emailMarketingService.processScheduledJob(job);
            } catch (Exception e) {
                log.warn("[MAIL] Job {} falhou: {}", job.getId(), e.getMessage());
            }
        }
    }

    /** Diário: usuários sem login há 14+ dias; não reenvia se já enviou nos últimos 30 dias. */
    @Scheduled(cron = "0 40 11 * * *")
    @Transactional
    public void sendReactivationCampaign() {
        OffsetDateTime cutoff = OffsetDateTime.now(ZoneOffset.UTC).minusDays(14);
        OffsetDateTime cooldown = OffsetDateTime.now(ZoneOffset.UTC).minusDays(30);
        List<User> users = userRepository.findInactiveForReactivation(cutoff, cooldown);
        for (User u : users) {
            try {
                emailMarketingService.sendReactivation(u);
            } catch (Exception e) {
                log.warn("[MAIL] Reativação falhou para usuário {}: {}", u.getId(), e.getMessage());
            }
        }
    }
}
