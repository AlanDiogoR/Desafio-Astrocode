package com.astrocode.backend.domain.jobs;

import com.astrocode.backend.domain.entities.DataDeletionRequest;
import com.astrocode.backend.domain.entities.ScheduledMarketingEmail;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.repositories.DataDeletionRequestRepository;
import com.astrocode.backend.domain.repositories.ScheduledMarketingEmailRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import com.astrocode.backend.domain.services.EmailMarketingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Processa e-mails de marketing agendados, campanha de reativação e exclusão de dados.
 */
@Component
public class GrivyScheduledJobs {

    private static final Logger log = LoggerFactory.getLogger(GrivyScheduledJobs.class);

    private final ScheduledMarketingEmailRepository scheduledMarketingEmailRepository;
    private final EmailMarketingService emailMarketingService;
    private final UserRepository userRepository;
    private final DataDeletionRequestRepository deletionRequestRepository;

    public GrivyScheduledJobs(
            ScheduledMarketingEmailRepository scheduledMarketingEmailRepository,
            EmailMarketingService emailMarketingService,
            UserRepository userRepository,
            DataDeletionRequestRepository deletionRequestRepository) {
        this.scheduledMarketingEmailRepository = scheduledMarketingEmailRepository;
        this.emailMarketingService = emailMarketingService;
        this.userRepository = userRepository;
        this.deletionRequestRepository = deletionRequestRepository;
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

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void processPendingDeletions() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<DataDeletionRequest> pending = deletionRequestRepository
                .findByStatusAndScheduledForBefore("pending", now);

        for (DataDeletionRequest req : pending) {
            try {
                userRepository.findByMetaUserId(req.getMetaUserId())
                        .ifPresent(user -> {
                            user.setScheduledForDeletion(true);
                            userRepository.save(user);
                        });

                req.setStatus("completed");
                req.setCompletedAt(now);
                deletionRequestRepository.save(req);

                log.info("Data deletion completed for request {}", req.getConfirmationCode());
            } catch (Exception e) {
                log.error("Failed to process deletion for {}", req.getConfirmationCode(), e);
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
