package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.ScheduledMarketingEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ScheduledMarketingEmailRepository extends JpaRepository<ScheduledMarketingEmail, UUID> {

    @Query("SELECT e FROM ScheduledMarketingEmail e WHERE e.sentAt IS NULL AND e.cancelled = false AND e.scheduledAt <= :now ORDER BY e.scheduledAt ASC")
    List<ScheduledMarketingEmail> findDue(@Param("now") OffsetDateTime now);

    boolean existsByUser_IdAndCampaignAndCancelledIsFalse(UUID userId, String campaign);

    @Modifying
    @Transactional
    @Query("UPDATE ScheduledMarketingEmail e SET e.cancelled = true WHERE e.user.id = :userId AND e.sentAt IS NULL AND e.cancelled = false AND e.campaign LIKE 'ONBOARDING%'")
    int cancelPendingOnboardingForUser(@Param("userId") UUID userId);
}
