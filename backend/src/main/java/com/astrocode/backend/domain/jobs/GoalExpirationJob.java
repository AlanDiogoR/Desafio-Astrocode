package com.astrocode.backend.domain.jobs;

import com.astrocode.backend.domain.entities.SavingsGoal;
import com.astrocode.backend.domain.model.enums.GoalStatus;
import com.astrocode.backend.domain.repositories.SavingsGoalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class GoalExpirationJob {

    private static final Logger log = LoggerFactory.getLogger(GoalExpirationJob.class);

    private final SavingsGoalRepository savingsGoalRepository;

    public GoalExpirationJob(SavingsGoalRepository savingsGoalRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
    }

    @Scheduled(cron = "0 0 1 * * ?") // 1h da manhã todo dia
    @Transactional
    public void expireOverdueGoals() {
        var expired = savingsGoalRepository.findByStatusAndEndDateBeforeAndDeletedAtIsNull(
                GoalStatus.ACTIVE, LocalDate.now());
        expired.forEach(g -> {
            g.setStatus(GoalStatus.CANCELLED);
            log.info("Goal expired: id={}, name={}", g.getId(), g.getName());
        });
        if (!expired.isEmpty()) {
            savingsGoalRepository.saveAll(expired);
        }
    }
}
