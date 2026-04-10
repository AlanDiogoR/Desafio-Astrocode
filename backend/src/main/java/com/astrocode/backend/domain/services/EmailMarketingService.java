package com.astrocode.backend.domain.services;

import com.astrocode.backend.domain.entities.ScheduledMarketingEmail;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.repositories.ScheduledMarketingEmailRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import com.astrocode.backend.infrastructure.email.GrivyEmailTemplates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 * E-mails de marketing transacional (boas-vindas, onboarding, reativação, Open Finance).
 */
@Service
public class EmailMarketingService {

    private static final Logger log = LoggerFactory.getLogger(EmailMarketingService.class);

    public static final String CAMP_ONBOARD_D1 = "ONBOARDING_D1";
    public static final String CAMP_ONBOARD_D3 = "ONBOARDING_D3";
    public static final String CAMP_ONBOARD_D7 = "ONBOARDING_D7";

    private final MailService mailService;
    private final ScheduledMarketingEmailRepository scheduledMarketingEmailRepository;
    private final UserRepository userRepository;

    @Value("${app.frontend-url:https://grivy.netlify.app}")
    private String frontendUrl;

    public EmailMarketingService(
            MailService mailService,
            ScheduledMarketingEmailRepository scheduledMarketingEmailRepository,
            UserRepository userRepository) {
        this.mailService = mailService;
        this.scheduledMarketingEmailRepository = scheduledMarketingEmailRepository;
        this.userRepository = userRepository;
    }

    /**
     * Disparado após confirmação de e-mail (cadastro verificado).
     */
    @Transactional
    public void onEmailVerified(User user) {
        sendWelcome(user);
        scheduleOnboarding(user);
    }

    /**
     * Envia e-mail de boas-vindas imediatamente.
     */
    public void sendWelcome(User user) {
        if (user.isMarketingEmailsOptOut()) {
            return;
        }
        String first = firstName(user.getName());
        String subject = GrivyEmailTemplates.welcomeSubject(first);
        var pair = GrivyEmailTemplates.welcome(first, baseUrl());
        mailService.sendHtmlAndText(user.getEmail(), subject, pair.html(), pair.text());
    }

    /**
     * Agenda a sequência drip D+1, D+3, D+7.
     */
    @Transactional
    public void scheduleOnboarding(User user) {
        if (user.isMarketingEmailsOptOut()) {
            return;
        }
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        scheduleIfAbsent(user, CAMP_ONBOARD_D1, now.plusDays(1));
        scheduleIfAbsent(user, CAMP_ONBOARD_D3, now.plusDays(3));
        scheduleIfAbsent(user, CAMP_ONBOARD_D7, now.plusDays(7));
    }

    private void scheduleIfAbsent(User user, String campaign, OffsetDateTime when) {
        if (scheduledMarketingEmailRepository.existsByUser_IdAndCampaignAndCancelledIsFalse(user.getId(), campaign)) {
            return;
        }
        ScheduledMarketingEmail row = ScheduledMarketingEmail.builder()
                .user(user)
                .campaign(campaign)
                .scheduledAt(when)
                .cancelled(false)
                .build();
        scheduledMarketingEmailRepository.save(row);
    }

    /**
     * Cancela e-mails de onboarding pendentes (ex.: opt-out de marketing).
     */
    @Transactional
    public void cancelPendingOnboarding(UUID userId) {
        scheduledMarketingEmailRepository.cancelPendingOnboardingForUser(userId);
    }

    public void sendReactivation(User user) {
        if (user.isMarketingEmailsOptOut()) {
            return;
        }
        String first = firstName(user.getName());
        String subject = GrivyEmailTemplates.reactivationSubject(first);
        var pair = GrivyEmailTemplates.reactivation(first, baseUrl());
        mailService.sendHtmlAndText(user.getEmail(), subject, pair.html(), pair.text());
        user.setLastReactivationEmailAt(OffsetDateTime.now(ZoneOffset.UTC));
        userRepository.save(user);
    }

    public void sendOpenFinanceWaitlistConfirmation(User user) {
        sendOpenFinanceWaitlistToEmail(user.getEmail(), firstName(user.getName()));
    }

    /**
     * Confirmação de lista de espera para quem não está autenticado (apenas e-mail informado).
     */
    public void sendOpenFinanceWaitlistToEmail(String email, String firstNameOrGeneric) {
        String first = firstNameOrGeneric != null && !firstNameOrGeneric.isBlank() ? firstNameOrGeneric : "Cliente";
        String subject = GrivyEmailTemplates.openFinanceWaitlistSubject();
        var pair = GrivyEmailTemplates.openFinanceWaitlist(first, baseUrl());
        mailService.sendHtmlAndText(email, subject, pair.html(), pair.text());
    }

    public void sendOpenFinanceReleased(List<String> emails) {
        for (String email : emails) {
            try {
                String subject = GrivyEmailTemplates.openFinanceReleasedSubject();
                var pair = GrivyEmailTemplates.openFinanceReleased("Cliente", baseUrl());
                mailService.sendHtmlAndText(email, subject, pair.html(), pair.text());
            } catch (Exception e) {
                log.warn("[MAIL] Falha Open Finance released para {}: {}", redactEmail(email), e.getMessage());
            }
        }
    }

    /**
     * Processa um job agendado (chamado pelo scheduler).
     */
    @Transactional
    public void processScheduledJob(ScheduledMarketingEmail job) {
        User user = userRepository.findByIdWithSubscription(job.getUser().getId())
                .orElse(job.getUser());
        if (user.isMarketingEmailsOptOut()) {
            job.setCancelled(true);
            scheduledMarketingEmailRepository.save(job);
            return;
        }
        String first = firstName(user.getName());
        String subject;
        GrivyEmailTemplates.HtmlTextPair pair;
        switch (job.getCampaign()) {
            case CAMP_ONBOARD_D1 -> {
                subject = GrivyEmailTemplates.onboardingD1Subject();
                pair = GrivyEmailTemplates.onboardingD1(first, baseUrl());
            }
            case CAMP_ONBOARD_D3 -> {
                subject = GrivyEmailTemplates.onboardingD3Subject();
                pair = GrivyEmailTemplates.onboardingD3(first, baseUrl());
            }
            case CAMP_ONBOARD_D7 -> {
                subject = GrivyEmailTemplates.onboardingD7Subject();
                pair = GrivyEmailTemplates.onboardingD7(first, baseUrl());
            }
            default -> {
                log.warn("[MAIL] Campanha desconhecida: {}", job.getCampaign());
                job.setCancelled(true);
                scheduledMarketingEmailRepository.save(job);
                return;
            }
        }
        mailService.sendHtmlAndText(user.getEmail(), subject, pair.html(), pair.text());
        job.setSentAt(OffsetDateTime.now(ZoneOffset.UTC));
        scheduledMarketingEmailRepository.save(job);
    }

    private String baseUrl() {
        String base = frontendUrl != null ? frontendUrl.replaceAll("/$", "") : "https://grivy.netlify.app";
        return base;
    }

    private static String firstName(String name) {
        if (name == null || name.isBlank()) {
            return "Cliente";
        }
        String[] p = name.trim().split("\\s+");
        return p[0];
    }

    private static String redactEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        return email.substring(0, 1) + "***@" + email.substring(email.indexOf('@') + 1);
    }
}
