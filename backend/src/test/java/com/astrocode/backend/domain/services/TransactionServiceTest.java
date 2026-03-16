package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.transaction.TransactionRequest;
import com.astrocode.backend.api.dto.transaction.TransactionUpdateRequest;
import com.astrocode.backend.domain.entities.BankAccount;
import com.astrocode.backend.domain.entities.Category;
import com.astrocode.backend.domain.entities.SavingsGoal;
import com.astrocode.backend.domain.entities.Transaction;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.*;
import com.astrocode.backend.domain.model.enums.AccountType;
import com.astrocode.backend.domain.model.enums.GoalStatus;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.repositories.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService - Unidade")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SavingsGoalRepository savingsGoalRepository;

    @Mock
    private CreditCardService creditCardService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private final UUID userId = UUID.randomUUID();
    private final User user = User.builder().id(userId).name("Test").email("test@test.com").password("x").build();
    private final UUID accountId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final BankAccount bankAccount = BankAccount.builder()
            .id(accountId)
            .user(user)
            .name("Conta")
            .initialBalance(BigDecimal.valueOf(1000))
            .currentBalance(BigDecimal.valueOf(1000))
            .type(AccountType.CHECKING)
            .build();
    private final Category category = Category.builder()
            .id(categoryId)
            .user(user)
            .name("Moradia")
            .type(TransactionType.EXPENSE)
            .build();

    @Test
    @DisplayName("create_income_incrementsAccountBalance: INCOME soma ao currentBalance")
    void create_income_incrementsAccountBalance() {
        var request = new TransactionRequest(
                "Salário", BigDecimal.valueOf(5000), LocalDate.now(), TransactionType.INCOME,
                accountId, categoryId, null, false, null);
        when(userRepository.findByIdWithSubscription(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.countByUserIdAndDateBetween(any(), any(), any())).thenReturn(0L);
        when(bankAccountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(bankAccount));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            var t = inv.getArgument(0, Transaction.class);
            t.setId(UUID.randomUUID());
            return t;
        });
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = transactionService.create(request, userId);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(5000));
        verify(bankAccountRepository).save(argThat(acc -> acc.getCurrentBalance().compareTo(BigDecimal.valueOf(6000)) == 0));
    }

    @Test
    @DisplayName("create_expense_decrementsAccountBalance: EXPENSE subtrai do currentBalance")
    void create_expense_decrementsAccountBalance() {
        var request = new TransactionRequest(
                "Aluguel", BigDecimal.valueOf(300), LocalDate.now(), TransactionType.EXPENSE,
                accountId, categoryId, null, false, null);
        when(userRepository.findByIdWithSubscription(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.countByUserIdAndDateBetween(any(), any(), any())).thenReturn(0L);
        when(bankAccountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(bankAccount));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            var t = inv.getArgument(0, Transaction.class);
            t.setId(UUID.randomUUID());
            return t;
        });
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = transactionService.create(request, userId);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(TransactionType.EXPENSE);
        verify(bankAccountRepository).save(argThat(acc -> acc.getCurrentBalance().compareTo(BigDecimal.valueOf(700)) == 0));
    }

    @Test
    @DisplayName("create_expense_insufficientBalance_throws: saldo insuficiente lança exceção")
    void create_expense_insufficientBalance_throws() {
        bankAccount.setCurrentBalance(BigDecimal.valueOf(100));
        var request = new TransactionRequest(
                "Compra", BigDecimal.valueOf(500), LocalDate.now(), TransactionType.EXPENSE,
                accountId, categoryId, null, false, null);
        when(userRepository.findByIdWithSubscription(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.countByUserIdAndDateBetween(any(), any(), any())).thenReturn(0L);
        when(bankAccountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(bankAccount));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> transactionService.create(request, userId))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Saldo insuficiente");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("create_wrongCategory_throws: categoria de tipo errado lança exceção")
    void create_wrongCategory_throws() {
        var incomeCategory = Category.builder()
                .id(categoryId)
                .user(user)
                .name("Salário")
                .type(TransactionType.INCOME)
                .build();
        var request = new TransactionRequest(
                "Despesa", BigDecimal.valueOf(100), LocalDate.now(), TransactionType.EXPENSE,
                accountId, categoryId, null, false, null);
        when(userRepository.findByIdWithSubscription(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.countByUserIdAndDateBetween(any(), any(), any())).thenReturn(0L);
        when(bankAccountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(bankAccount));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(incomeCategory));

        assertThatThrownBy(() -> transactionService.create(request, userId))
                .isInstanceOf(CategoryTypeMismatchException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("create_accountNotOwned_throws: conta de outro usuário lança exceção")
    void create_accountNotOwned_throws() {
        var otherUser = User.builder().id(UUID.randomUUID()).name("Other").email("other@test.com").password("x").build();
        bankAccount.setUser(otherUser);
        var request = new TransactionRequest(
                "Despesa", BigDecimal.valueOf(100), LocalDate.now(), TransactionType.EXPENSE,
                accountId, categoryId, null, false, null);
        when(userRepository.findByIdWithSubscription(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.countByUserIdAndDateBetween(any(), any(), any())).thenReturn(0L);
        when(bankAccountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(bankAccount));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> transactionService.create(request, userId))
                .isInstanceOf(AccountNotOwnedException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete_reverts_accountBalance: reverte saldo ao deletar")
    void delete_reverts_accountBalance() {
        var txId = UUID.randomUUID();
        bankAccount.setCurrentBalance(BigDecimal.valueOf(500));
        var transaction = Transaction.builder()
                .id(txId)
                .user(user)
                .bankAccount(bankAccount)
                .category(category)
                .name("Despesa")
                .amount(BigDecimal.valueOf(200))
                .date(LocalDate.now())
                .type(TransactionType.EXPENSE)
                .build();
        when(transactionRepository.findById(txId)).thenReturn(Optional.of(transaction));
        when(bankAccountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(bankAccount));
        doNothing().when(transactionRepository).delete(transaction);

        transactionService.delete(txId, userId);

        verify(bankAccountRepository).saveAndFlush(argThat(acc ->
                acc.getCurrentBalance().compareTo(BigDecimal.valueOf(700)) == 0));
        verify(transactionRepository).delete(transaction);
    }

    @Test
    @DisplayName("delete_withGoal_revertsGoalAmount: reverte goal.currentAmount ao deletar")
    void delete_withGoal_revertsGoalAmount() {
        var txId = UUID.randomUUID();
        var goal = SavingsGoal.builder()
                .id(UUID.randomUUID())
                .user(user)
                .name("Meta")
                .targetAmount(BigDecimal.valueOf(1000))
                .currentAmount(BigDecimal.valueOf(300))
                .status(GoalStatus.ACTIVE)
                .startDate(LocalDate.now())
                .build();
        var transaction = Transaction.builder()
                .id(txId)
                .user(user)
                .bankAccount(bankAccount)
                .category(category)
                .name("Aporte")
                .amount(BigDecimal.valueOf(100))
                .date(LocalDate.now())
                .type(TransactionType.EXPENSE)
                .goal(goal)
                .build();
        when(transactionRepository.findById(txId)).thenReturn(Optional.of(transaction));
        when(bankAccountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(bankAccount));
        when(savingsGoalRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        transactionService.delete(txId, userId);

        verify(savingsGoalRepository).saveAndFlush(argThat(g ->
                g.getCurrentAmount().compareTo(BigDecimal.valueOf(200)) == 0));
        verify(transactionRepository).delete(transaction);
    }
}
