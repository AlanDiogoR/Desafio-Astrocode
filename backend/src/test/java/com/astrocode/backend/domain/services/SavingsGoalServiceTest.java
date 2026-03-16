package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.goal.SavingsGoalContributeRequest;
import com.astrocode.backend.api.dto.goal.SavingsGoalRequest;
import com.astrocode.backend.api.dto.goal.SavingsGoalWithdrawRequest;
import com.astrocode.backend.domain.entities.BankAccount;
import com.astrocode.backend.domain.entities.Category;
import com.astrocode.backend.domain.entities.SavingsGoal;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.ResourceAccessDeniedException;
import com.astrocode.backend.domain.model.enums.AccountType;
import com.astrocode.backend.domain.model.enums.GoalStatus;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.repositories.BankAccountRepository;
import com.astrocode.backend.domain.repositories.CategoryRepository;
import com.astrocode.backend.domain.repositories.SavingsGoalRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SavingsGoalService - Unidade")
class SavingsGoalServiceTest {

    @Mock
    private SavingsGoalRepository savingsGoalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private SavingsGoalService savingsGoalService;

    private final UUID userId = UUID.randomUUID();
    private final User user = User.builder().id(userId).name("Test").email("test@test.com").password("x").build();

    @Test
    @DisplayName("createGoal_success: salva e retorna a meta criada")
    void createGoal_success() {
        var request = new SavingsGoalRequest("Viagem", BigDecimal.valueOf(5000), LocalDate.of(2025, 12, 31), "#087f5b");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenAnswer(inv -> {
            var goal = inv.getArgument(0, SavingsGoal.class);
            goal.setId(UUID.randomUUID());
            return goal;
        });

        var result = savingsGoalService.create(request, userId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Viagem");
        assertThat(result.getTargetAmount()).isEqualByComparingTo(BigDecimal.valueOf(5000));
        assertThat(result.getCurrentAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(savingsGoalRepository).save(any(SavingsGoal.class));
    }

    @Test
    @DisplayName("contribute_exceedsTarget: contribuição ultrapassa targetAmount, lança exceção")
    void contribute_exceedsTarget() {
        var goalId = UUID.randomUUID();
        var goal = SavingsGoal.builder()
                .id(goalId)
                .user(user)
                .name("Meta")
                .targetAmount(BigDecimal.valueOf(1000))
                .currentAmount(BigDecimal.valueOf(500))
                .status(GoalStatus.ACTIVE)
                .startDate(LocalDate.now())
                .build();
        var request = new SavingsGoalContributeRequest(UUID.randomUUID(), BigDecimal.valueOf(600)); // 500+600 > 1000

        when(savingsGoalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        assertThatThrownBy(() -> savingsGoalService.contribute(goalId, request, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ultrapassar");
    }

    @Test
    @DisplayName("withdraw_insufficientFunds: saque maior que currentAmount, lança exceção")
    void withdraw_insufficientFunds() {
        var goalId = UUID.randomUUID();
        var goal = SavingsGoal.builder()
                .id(goalId)
                .user(user)
                .name("Meta")
                .targetAmount(BigDecimal.valueOf(1000))
                .currentAmount(BigDecimal.valueOf(200))
                .status(GoalStatus.ACTIVE)
                .startDate(LocalDate.now())
                .build();
        var bankAccountId = UUID.randomUUID();
        var request = new SavingsGoalWithdrawRequest(bankAccountId, BigDecimal.valueOf(300)); // 300 > 200

        when(savingsGoalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        assertThatThrownBy(() -> savingsGoalService.withdraw(goalId, request, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maior que o saldo atual");
    }

    @Test
    @DisplayName("deleteGoal_notOwner: meta pertence a outro usuário, lança exceção")
    void deleteGoal_notOwner() {
        var goalId = UUID.randomUUID();
        var otherUser = User.builder().id(UUID.randomUUID()).name("Other").email("other@test.com").password("x").build();
        var goal = SavingsGoal.builder().id(goalId).user(otherUser).name("Meta").build();

        when(savingsGoalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        assertThatThrownBy(() -> savingsGoalService.delete(goalId, user))
                .isInstanceOf(ResourceAccessDeniedException.class);
    }
}
