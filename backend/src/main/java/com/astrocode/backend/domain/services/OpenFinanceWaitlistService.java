package com.astrocode.backend.domain.services;

import com.astrocode.backend.domain.entities.OpenFinanceWaitlistEntry;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.repositories.OpenFinanceWaitlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lista de espera do Open Finance e envio do e-mail de confirmação (Template 4).
 */
@Service
public class OpenFinanceWaitlistService {

    private final OpenFinanceWaitlistRepository waitlistRepository;
    private final EmailMarketingService emailMarketingService;

    public OpenFinanceWaitlistService(
            OpenFinanceWaitlistRepository waitlistRepository,
            EmailMarketingService emailMarketingService) {
        this.waitlistRepository = waitlistRepository;
        this.emailMarketingService = emailMarketingService;
    }

    @Transactional
    public void join(String email, User userOrNull) {
        String normalized = email.trim().toLowerCase();
        if (waitlistRepository.existsByEmailIgnoreCase(normalized)) {
            return;
        }
        OpenFinanceWaitlistEntry entry = OpenFinanceWaitlistEntry.builder()
                .email(normalized)
                .user(userOrNull)
                .build();
        waitlistRepository.save(entry);
        if (userOrNull != null) {
            emailMarketingService.sendOpenFinanceWaitlistConfirmation(userOrNull);
        } else {
            emailMarketingService.sendOpenFinanceWaitlistToEmail(normalized, "Cliente");
        }
    }
}
