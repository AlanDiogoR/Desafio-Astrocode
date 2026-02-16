package com.astrocode.backend.domain.jobs;

import com.astrocode.backend.domain.entities.BankAccount;
import com.astrocode.backend.domain.entities.Category;
import com.astrocode.backend.domain.entities.Transaction;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.model.enums.RecurrenceFrequency;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.repositories.TransactionRepository;
import com.astrocode.backend.domain.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecurringTransactionJob")
class RecurringTransactionJobTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private RecurringTransactionJob job;

    private Transaction parentTransaction;

    @BeforeEach
    void setUp() {
        var user = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .password("encoded")
                .build();

        var bankAccount = BankAccount.builder()
                .id(UUID.randomUUID())
                .user(user)
                .name("Conta")
                .initialBalance(BigDecimal.ZERO)
                .currentBalance(BigDecimal.valueOf(1000))
                .build();

        var category = Category.builder()
                .id(UUID.randomUUID())
                .user(user)
                .name("Mercado")
                .type(TransactionType.EXPENSE)
                .build();

        parentTransaction = Transaction.builder()
                .id(UUID.randomUUID())
                .user(user)
                .bankAccount(bankAccount)
                .category(category)
                .name("Aluguel")
                .amount(BigDecimal.valueOf(1500))
                .date(LocalDate.of(2025, 1, 5))
                .type(TransactionType.EXPENSE)
                .isRecurring(true)
                .frequency(RecurrenceFrequency.MONTHLY)
                .parentTransaction(null)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar transação filha quando pai recorrente existe e não há filha no mês atual")
    void shouldCreateChildWhenParentExistsAndNoChildForCurrentMonth() {
        when(transactionRepository.findParentRecurringTransactions())
                .thenReturn(List.of(parentTransaction));
        when(transactionRepository.existsChildForParentInDateRange(
                eq(parentTransaction.getId()),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(false);

        job.generateRecurringTransactions();

        ArgumentCaptor<Transaction> parentCaptor = ArgumentCaptor.forClass(Transaction.class);
        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(transactionService, times(1)).createRecurringChild(parentCaptor.capture(), dateCaptor.capture());

        assertThat(parentCaptor.getValue().getId()).isEqualTo(parentTransaction.getId());
        assertThat(dateCaptor.getValue().getMonth()).isEqualTo(LocalDate.now().getMonth());
        assertThat(dateCaptor.getValue().getYear()).isEqualTo(LocalDate.now().getYear());
    }

    @Test
    @DisplayName("Não deve criar transação quando já existe filha no período")
    void shouldNotCreateChildWhenChildAlreadyExists() {
        when(transactionRepository.findParentRecurringTransactions())
                .thenReturn(List.of(parentTransaction));
        when(transactionRepository.existsChildForParentInDateRange(
                eq(parentTransaction.getId()),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(true);

        job.generateRecurringTransactions();

        verify(transactionService, never()).createRecurringChild(any(), any());
    }

    @Test
    @DisplayName("Não deve fazer nada quando não há transações pai recorrentes")
    void shouldDoNothingWhenNoParentRecurringTransactions() {
        when(transactionRepository.findParentRecurringTransactions())
                .thenReturn(Collections.emptyList());

        job.generateRecurringTransactions();

        verify(transactionRepository, never()).existsChildForParentInDateRange(any(), any(), any());
        verify(transactionService, never()).createRecurringChild(any(), any());
    }
}
