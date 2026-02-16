package com.astrocode.backend.domain.services;

import com.astrocode.backend.domain.entities.BankAccount;
import com.astrocode.backend.domain.entities.Category;
import com.astrocode.backend.domain.entities.Transaction;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.model.enums.AccountType;
import com.astrocode.backend.domain.model.enums.RecurrenceFrequency;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.repositories.BankAccountRepository;
import com.astrocode.backend.domain.repositories.CategoryRepository;
import com.astrocode.backend.domain.repositories.TransactionRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("TransactionService - Recorrência")
class TransactionServiceRecurringTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private BankAccount bankAccount;
    private Category category;
    private Transaction parentTransaction;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Recurring Test User")
                .email("recurring.test@" + UUID.randomUUID() + ".com")
                .password(passwordEncoder.encode("senha123"))
                .build();
        user = userRepository.save(user);

        bankAccount = BankAccount.builder()
                .user(user)
                .name("Conta Corrente")
                .initialBalance(BigDecimal.valueOf(5000))
                .currentBalance(BigDecimal.valueOf(5000))
                .type(AccountType.CHECKING)
                .build();
        bankAccount = bankAccountRepository.save(bankAccount);

        category = Category.builder()
                .user(user)
                .name("Moradia")
                .type(TransactionType.EXPENSE)
                .build();
        category = categoryRepository.save(category);

        parentTransaction = Transaction.builder()
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
        parentTransaction = transactionRepository.save(parentTransaction);

        var currentBalance = bankAccount.getCurrentBalance().subtract(BigDecimal.valueOf(1500));
        bankAccount.setCurrentBalance(currentBalance);
        bankAccountRepository.save(bankAccount);
    }

    @Test
    @DisplayName("Deve criar transação filha vinculada ao pai e atualizar saldo")
    void shouldCreateRecurringChildAndUpdateBalance() {
        var targetDate = LocalDate.of(2025, 2, 5);
        var balanceBefore = bankAccountRepository.findById(bankAccount.getId())
                .orElseThrow().getCurrentBalance();

        var child = transactionService.createRecurringChild(parentTransaction, targetDate);

        assertThat(child).isNotNull();
        assertThat(child.getId()).isNotNull();
        assertThat(child.getParentTransaction().getId()).isEqualTo(parentTransaction.getId());
        assertThat(child.getName()).isEqualTo("Aluguel");
        assertThat(child.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1500));
        assertThat(child.getDate()).isEqualTo(targetDate);
        assertThat(child.getIsRecurring()).isFalse();
        assertThat(child.getFrequency()).isNull();

        var balanceAfter = bankAccountRepository.findById(bankAccount.getId())
                .orElseThrow().getCurrentBalance();
        assertThat(balanceAfter).isEqualByComparingTo(balanceBefore.subtract(BigDecimal.valueOf(1500)));
    }
}
