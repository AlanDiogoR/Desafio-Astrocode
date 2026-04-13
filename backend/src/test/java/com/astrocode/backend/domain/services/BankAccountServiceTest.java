package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.account.BankAccountRequest;
import com.astrocode.backend.domain.entities.BankAccount;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.DuplicateAccountNameException;
import com.astrocode.backend.domain.exceptions.ResourceAccessDeniedException;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import com.astrocode.backend.domain.model.enums.AccountType;
import com.astrocode.backend.domain.repositories.BankAccountRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BankAccountService - Unidade")
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private com.astrocode.backend.domain.repositories.SavingsGoalRepository savingsGoalRepository;

    @Mock
    private com.astrocode.backend.domain.repositories.TransactionRepository transactionRepository;

    @Mock
    private PlanLimitService planLimitService;

    @InjectMocks
    private BankAccountService bankAccountService;

    private final UUID userId = UUID.randomUUID();
    private final User user = User.builder().id(userId).name("Test").email("test@test.com").password("x").build();

    @Test
    @DisplayName("createAccount_success: dados válidos, salva e retorna a conta criada")
    void createAccount_success() {
        var request = new BankAccountRequest("Conta Corrente", BigDecimal.valueOf(1000), AccountType.CHECKING, "#087f5b");

        when(userRepository.findByIdWithSubscription(userId)).thenReturn(Optional.of(user));
        doNothing().when(planLimitService).checkFreePlanBankAccountLimit(userId);
        when(bankAccountRepository.findByUserIdAndNameIgnoreCase(userId, "Conta Corrente")).thenReturn(List.of());
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(inv -> {
            var acc = inv.getArgument(0, BankAccount.class);
            acc.setId(UUID.randomUUID());
            return acc;
        });

        var result = bankAccountService.create(request, userId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Conta Corrente");
        assertThat(result.getCurrentBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        verify(bankAccountRepository).save(any(BankAccount.class));
    }

    @Test
    @DisplayName("createAccount_duplicateName: já existe conta com o mesmo nome, lança DuplicateAccountNameException")
    void createAccount_duplicateName() {
        var request = new BankAccountRequest("Conta Corrente", BigDecimal.valueOf(1000), AccountType.CHECKING, null);
        var existing = BankAccount.builder().id(UUID.randomUUID()).name("Conta Corrente").user(user).build();

        when(userRepository.findByIdWithSubscription(userId)).thenReturn(Optional.of(user));
        doNothing().when(planLimitService).checkFreePlanBankAccountLimit(userId);
        when(bankAccountRepository.findByUserIdAndNameIgnoreCase(userId, "Conta Corrente")).thenReturn(List.of(existing));

        assertThatThrownBy(() -> bankAccountService.create(request, userId))
                .isInstanceOf(DuplicateAccountNameException.class)
                .hasMessageContaining("Conta Corrente");

        verify(bankAccountRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteAccount_notOwner: conta pertence a outro usuário, lança exceção de autorização")
    void deleteAccount_notOwner() {
        var accountId = UUID.randomUUID();
        var otherUser = User.builder().id(UUID.randomUUID()).name("Other").email("other@test.com").password("x").build();
        var account = BankAccount.builder().id(accountId).user(otherUser).build();

        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> bankAccountService.delete(accountId, user))
                .isInstanceOf(ResourceAccessDeniedException.class);

        verify(bankAccountRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteAccount_success: conta pertence ao usuário, deleta")
    void deleteAccount_success() {
        var accountId = UUID.randomUUID();
        var account = BankAccount.builder().id(accountId).user(user).build();

        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findGoalImpactRowsByBankAccountId(accountId)).thenReturn(List.of());
        doNothing().when(bankAccountRepository).delete(account);

        bankAccountService.delete(accountId, user);

        verify(bankAccountRepository).delete(account);
    }

    @Test
    @DisplayName("update_adjustsCurrentBalanceWhenInitialBalanceChanges: ao alterar initialBalance, currentBalance é recalculado")
    void update_adjustsCurrentBalanceWhenInitialBalanceChanges() {
        var accountId = UUID.randomUUID();
        var account = BankAccount.builder()
                .id(accountId)
                .user(user)
                .name("Conta")
                .initialBalance(BigDecimal.valueOf(1000))
                .currentBalance(BigDecimal.valueOf(800))
                .type(AccountType.CHECKING)
                .build();
        var request = new BankAccountRequest("Conta", BigDecimal.valueOf(2000), AccountType.CHECKING, null);

        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(bankAccountRepository.findByUserIdAndNameIgnoreCase(userId, "Conta")).thenReturn(List.of(account));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = bankAccountService.update(accountId, request, user);

        assertThat(result.getInitialBalance()).isEqualByComparingTo(BigDecimal.valueOf(2000));
        assertThat(result.getCurrentBalance()).isEqualByComparingTo(BigDecimal.valueOf(1800)); // 800 + (2000-1000)
    }

    @Test
    @DisplayName("delete_reverts_goalCurrentAmounts: ao deletar conta, reverte currentAmount das metas nas transações")
    void delete_reverts_goalCurrentAmounts() {
        var accountId = UUID.randomUUID();
        var goalId = UUID.randomUUID();
        var goal = com.astrocode.backend.domain.entities.SavingsGoal.builder()
                .id(goalId)
                .user(user)
                .name("Meta")
                .targetAmount(BigDecimal.valueOf(1000))
                .currentAmount(BigDecimal.valueOf(300))
                .status(com.astrocode.backend.domain.model.enums.GoalStatus.ACTIVE)
                .startDate(java.time.LocalDate.now())
                .build();
        var category = com.astrocode.backend.domain.entities.Category.builder()
                .id(UUID.randomUUID())
                .user(user)
                .name("Cat")
                .type(com.astrocode.backend.domain.model.enums.TransactionType.EXPENSE)
                .build();
        var transaction = com.astrocode.backend.domain.entities.Transaction.builder()
                .id(UUID.randomUUID())
                .user(user)
                .category(category)
                .name("Aporte")
                .amount(BigDecimal.valueOf(100))
                .date(java.time.LocalDate.now())
                .type(com.astrocode.backend.domain.model.enums.TransactionType.EXPENSE)
                .goal(goal)
                .build();
        var account = BankAccount.builder()
                .id(accountId)
                .user(user)
                .build();
        transaction.setBankAccount(account);
        account.setTransactions(new java.util.ArrayList<>(List.of(transaction)));

        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findGoalImpactRowsByBankAccountId(accountId)).thenReturn(List.<Object[]>of(
                new Object[]{goalId, com.astrocode.backend.domain.model.enums.TransactionType.EXPENSE, BigDecimal.valueOf(100)}
        ));
        when(savingsGoalRepository.findAllById(List.of(goalId))).thenReturn(List.of(goal));
        when(savingsGoalRepository.saveAll(any(Iterable.class))).thenAnswer(inv -> {
            Iterable<?> arg = inv.getArgument(0);
            return java.util.stream.StreamSupport.stream(arg.spliterator(), false).toList();
        });

        bankAccountService.delete(accountId, user);

        verify(transactionService).revertGoalAmount(eq(goal), eq(BigDecimal.valueOf(100)), eq(com.astrocode.backend.domain.model.enums.TransactionType.EXPENSE));
        verify(savingsGoalRepository).saveAll(any(Iterable.class));
        verify(bankAccountRepository).delete(account);
    }
}
