package com.astrocode.backend.domain.jobs;

import com.astrocode.backend.domain.entities.SavingsGoal;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.model.enums.GoalStatus;
import com.astrocode.backend.domain.repositories.SavingsGoalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GoalExpirationJob")
class GoalExpirationJobTest {

    @Mock
    private SavingsGoalRepository savingsGoalRepository;

    @InjectMocks
    private GoalExpirationJob goalExpirationJob;

    @Test
    @DisplayName("Apenas metas ACTIVE com endDate < hoje são atualizadas para CANCELLED")
    void expireOverdueGoals_onlyActiveWithEndDateBeforeToday() {
        var user = User.builder()
                .id(UUID.randomUUID())
                .name("Test")
                .email("test@test.com")
                .password("x")
                .build();

        var expiredGoal = SavingsGoal.builder()
                .id(UUID.randomUUID())
                .user(user)
                .name("Meta vencida")
                .targetAmount(java.math.BigDecimal.valueOf(1000))
                .currentAmount(java.math.BigDecimal.valueOf(500))
                .status(GoalStatus.ACTIVE)
                .startDate(LocalDate.now().minusMonths(2))
                .endDate(LocalDate.now().minusDays(1))
                .build();

        when(savingsGoalRepository.findByStatusAndEndDateBeforeAndDeletedAtIsNull(
                eq(GoalStatus.ACTIVE), any(LocalDate.class)))
                .thenReturn(List.of(expiredGoal));
        when(savingsGoalRepository.saveAll(any())).thenReturn(List.of(expiredGoal));

        goalExpirationJob.expireOverdueGoals();

        assertThat(expiredGoal.getStatus()).isEqualTo(GoalStatus.CANCELLED);
        verify(savingsGoalRepository).saveAll(any());
    }

    @Test
    @DisplayName("Metas COMPLETED ou CANCELLED não são alteradas")
    void expireOverdueGoals_doesNotTouchCompletedOrCancelled() {
        when(savingsGoalRepository.findByStatusAndEndDateBeforeAndDeletedAtIsNull(
                eq(GoalStatus.ACTIVE), any(LocalDate.class)))
                .thenReturn(List.of());

        goalExpirationJob.expireOverdueGoals();

        verify(savingsGoalRepository, never()).saveAll(any());
    }
}
