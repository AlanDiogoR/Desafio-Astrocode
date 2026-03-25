package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.transaction.CategoryExpenseItem;
import com.astrocode.backend.api.dto.transaction.MonthlySummaryResponse;
import com.astrocode.backend.api.dto.transaction.TransactionRequest;
import com.astrocode.backend.api.dto.transaction.TransactionUpdateRequest;
import com.astrocode.backend.domain.entities.BankAccount;
import com.astrocode.backend.domain.entities.Category;
import com.astrocode.backend.domain.entities.SavingsGoal;
import com.astrocode.backend.domain.entities.Transaction;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.entities.CreditCard;
import com.astrocode.backend.domain.entities.CreditCardBill;
import com.astrocode.backend.domain.exceptions.AccountNotOwnedException;
import com.astrocode.backend.domain.exceptions.CategoryTypeMismatchException;
import com.astrocode.backend.domain.exceptions.InsufficientBalanceException;
import com.astrocode.backend.domain.exceptions.InvalidTransactionSourceException;
import com.astrocode.backend.domain.exceptions.PlanUpgradeRequiredException;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import com.astrocode.backend.domain.model.enums.RecurrenceFrequency;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.model.enums.GoalStatus;
import com.astrocode.backend.domain.repositories.BankAccountRepository;
import com.astrocode.backend.domain.repositories.CategoryRepository;
import com.astrocode.backend.domain.repositories.CreditCardBillRepository;
import com.astrocode.backend.domain.repositories.SavingsGoalRepository;
import com.astrocode.backend.domain.repositories.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CreditCardBillRepository creditCardBillRepository;
    private final CategoryRepository categoryRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final CreditCardService creditCardService;
    private final PlanLimitService planLimitService;

    public TransactionService(
            TransactionRepository transactionRepository,
            BankAccountRepository bankAccountRepository,
            CreditCardBillRepository creditCardBillRepository,
            CategoryRepository categoryRepository,
            SavingsGoalRepository savingsGoalRepository,
            CreditCardService creditCardService,
            PlanLimitService planLimitService
    ) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.creditCardBillRepository = creditCardBillRepository;
        this.categoryRepository = categoryRepository;
        this.savingsGoalRepository = savingsGoalRepository;
        this.creditCardService = creditCardService;
        this.planLimitService = planLimitService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Transaction create(TransactionRequest request, UUID userId) {
        boolean hasBankAccount = request.bankAccountId() != null;
        boolean hasCreditCard = request.creditCardId() != null;

        if (hasBankAccount == hasCreditCard) {
            throw new InvalidTransactionSourceException();
        }

        planLimitService.checkFreePlanTransactionLimit(userId);

        if (hasCreditCard) {
            return createCreditCardTransaction(request, userId);
        }

        return createBankAccountTransaction(request, userId);
    }

    private Transaction createCreditCardTransaction(TransactionRequest request, UUID userId) {
        if (request.type() != TransactionType.EXPENSE) {
            throw new CategoryTypeMismatchException(
                    "Transações no cartão de crédito devem ser do tipo EXPENSE (despesa)"
            );
        }

        var creditCard = creditCardService.findById(request.creditCardId(), userId);
        var bill = creditCardService.getOrCreateCurrentBill(request.creditCardId(), userId);
        bill = creditCardBillRepository.findByIdForUpdate(bill.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada"));

        var newBillTotal = bill.getTotalAmount().add(request.amount());
        if (newBillTotal.compareTo(creditCard.getCreditLimit()) > 0) {
            var disponivel = creditCard.getCreditLimit().subtract(bill.getTotalAmount());
            throw new InsufficientBalanceException(
                    "Limite do cartão excedido. Disponível: R$ " + disponivel);
        }

        var category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        validateCategoryOwnership(category, userId);
        validateCategoryTypeMatch(request.type(), category.getType());

        var isRecurring = request.isRecurring() != null && request.isRecurring();
        var frequency = isRecurring && request.frequency() != null ? request.frequency() : RecurrenceFrequency.MONTHLY;

        var transaction = Transaction.builder()
                .user(creditCard.getUser())
                .bankAccount(null)
                .creditCard(creditCard)
                .creditCardBill(bill)
                .category(category)
                .name(request.name())
                .amount(request.amount())
                .date(request.date())
                .type(request.type())
                .isRecurring(isRecurring)
                .frequency(isRecurring ? frequency : null)
                .build();

        var savedTransaction = transactionRepository.save(transaction);

        bill.setTotalAmount(bill.getTotalAmount().add(request.amount()));
        creditCard.setCurrentBillAmount(creditCard.getCurrentBillAmount().add(request.amount()));

        return savedTransaction;
    }

    private Transaction createBankAccountTransaction(TransactionRequest request, UUID userId) {
        var bankAccount = bankAccountRepository.findByIdForUpdate(request.bankAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));

        validateAccountOwnership(bankAccount, userId);

        var category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        validateCategoryOwnership(category, userId);
        validateCategoryTypeMatch(request.type(), category.getType());

        var isRecurring = request.isRecurring() != null && request.isRecurring();
        var frequency = isRecurring && request.frequency() != null ? request.frequency() : RecurrenceFrequency.MONTHLY;

        var transaction = Transaction.builder()
                .user(bankAccount.getUser())
                .bankAccount(bankAccount)
                .category(category)
                .name(request.name())
                .amount(request.amount())
                .date(request.date())
                .type(request.type())
                .isRecurring(isRecurring)
                .frequency(isRecurring ? frequency : null)
                .build();

        var savedTransaction = transactionRepository.save(transaction);
        updateAccountBalance(bankAccount, request.amount(), request.type());
        bankAccountRepository.save(bankAccount);

        return savedTransaction;
    }

    @Transactional(rollbackFor = Exception.class)
    public Transaction createGoalTransaction(
            BankAccount bankAccount,
            Category category,
            String name,
            BigDecimal amount,
            TransactionType type,
            User user,
            SavingsGoal goal
    ) {
        planLimitService.checkFreePlanTransactionLimit(user.getId());

        var account = bankAccountRepository.findByIdForUpdate(bankAccount.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));
        validateAccountOwnership(account, user.getId());
        bankAccount = account;
        validateCategoryOwnership(category, user.getId());
        validateCategoryTypeMatch(type, category.getType());

        var transaction = Transaction.builder()
                .user(user)
                .bankAccount(bankAccount)
                .category(category)
                .name(name)
                .amount(amount)
                .date(java.time.LocalDate.now())
                .type(type)
                .goal(goal)
                .build();

        var saved = transactionRepository.save(transaction);
        updateAccountBalance(bankAccount, amount, type);
        bankAccountRepository.save(bankAccount);
        return saved;
    }

    public List<Transaction> findAllByUserId(UUID userId, Integer year, Integer month, UUID bankAccountId, TransactionType type) {
        return findAllByUserIdPaginated(userId, year, month, bankAccountId, type, Pageable.unpaged()).getContent();
    }

    public Page<Transaction> findAllByUserIdPaginated(UUID userId, Integer year, Integer month, UUID bankAccountId, TransactionType type, Pageable pageable) {
        var sort = Sort.by("date").descending().and(Sort.by("createdAt").descending());
        var actualPageable = pageable.isUnpaged() ? pageable : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (bankAccountId != null) {
            var bankAccount = bankAccountRepository.findById(bankAccountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));
            validateAccountOwnership(bankAccount, userId);

            if (year != null && month != null) {
                var startDate = LocalDate.of(year, month, 1);
                var endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                if (type != null) {
                    return transactionRepository.findByUserIdAndBankAccountIdAndDateYearAndDateMonthAndType(userId, bankAccountId, startDate, endDate, type, actualPageable);
                }
                return transactionRepository.findByUserIdAndBankAccountIdAndDateYearAndDateMonth(userId, bankAccountId, startDate, endDate, actualPageable);
            }
            if (type != null) {
                return transactionRepository.findByUserIdAndBankAccountIdAndType(userId, bankAccountId, type, actualPageable);
            }
            return transactionRepository.findByUserIdAndBankAccountId(userId, bankAccountId, actualPageable);
        }

        if (year != null && month != null) {
            var startDate = LocalDate.of(year, month, 1);
            var endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            if (type != null) {
                return transactionRepository.findByUserIdAndDateYearAndDateMonthAndType(userId, startDate, endDate, type, actualPageable);
            }
            return transactionRepository.findByUserIdAndDateYearAndDateMonth(userId, startDate, endDate, actualPageable);
        }

        if (type != null) {
            return transactionRepository.findByUserIdAndType(userId, type, actualPageable);
        }

        return transactionRepository.findByUserId(userId, actualPageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public Transaction update(UUID transactionId, TransactionUpdateRequest request, UUID userId) {
        var transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new AccountNotOwnedException("Você não tem permissão para acessar esta transação");
        }

        var oldAmount = transaction.getAmount();
        var newAmount = request.amount() != null ? request.amount() : oldAmount;

        if (transaction.getCreditCard() != null) {
            updateCreditCardTransaction(transaction, request, userId, oldAmount, newAmount);
            return transactionRepository.save(transaction);
        }

        var oldBankAccount = bankAccountRepository.findByIdForUpdate(transaction.getBankAccount().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));
        if (!oldBankAccount.getUser().getId().equals(userId)) {
            throw new AccountNotOwnedException("Você não tem permissão para acessar esta transação");
        }
        var oldType = transaction.getType();
        revertAccountBalance(oldBankAccount, oldAmount, oldType);

        var oldGoal = transaction.getGoal();
        if (oldGoal != null) {
            revertGoalAmount(oldGoal, oldAmount, oldType);
            savingsGoalRepository.save(oldGoal);
        }

        var newBankAccount = oldBankAccount;
        if (request.bankAccountId() != null && !request.bankAccountId().equals(oldBankAccount.getId())) {
            newBankAccount = bankAccountRepository.findByIdForUpdate(request.bankAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));
            validateAccountOwnership(newBankAccount, userId);
        }

        var newCategory = transaction.getCategory();
        if (request.categoryId() != null && !request.categoryId().equals(transaction.getCategory().getId())) {
            newCategory = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            validateCategoryOwnership(newCategory, userId);
        }

        var newType = request.type() != null ? request.type() : transaction.getType();
        if (request.categoryId() != null || request.type() != null) {
            validateCategoryTypeMatch(newType, newCategory.getType());
        }

        if (request.name() != null) transaction.setName(request.name());
        if (request.amount() != null) transaction.setAmount(request.amount());
        if (request.date() != null) transaction.setDate(request.date());
        if (request.isRecurring() != null) {
            transaction.setIsRecurring(request.isRecurring());
            transaction.setFrequency(request.isRecurring() && request.frequency() != null ? request.frequency() : null);
        }
        transaction.setType(newType);
        transaction.setBankAccount(newBankAccount);
        transaction.setCategory(newCategory);

        updateAccountBalance(newBankAccount, newAmount, newType);

        var goal = transaction.getGoal();
        if (goal != null) {
            applyGoalAmount(goal, newAmount, newType);
            savingsGoalRepository.save(goal);
        }

        if (!oldBankAccount.getId().equals(newBankAccount.getId())) {
            bankAccountRepository.saveAndFlush(oldBankAccount);
        }
        bankAccountRepository.saveAndFlush(newBankAccount);

        return transactionRepository.save(transaction);
    }

    private void updateCreditCardTransaction(Transaction transaction, TransactionUpdateRequest request,
                                             UUID userId, BigDecimal oldAmount, BigDecimal newAmount) {
        var creditCard = transaction.getCreditCard();
        var bill = transaction.getCreditCardBill();

        if (!creditCard.getUser().getId().equals(userId)) {
            throw new AccountNotOwnedException("Você não tem permissão para acessar esta transação");
        }

        if (bill != null) {
            bill.setTotalAmount(bill.getTotalAmount().subtract(oldAmount).add(newAmount));
        }
        creditCard.setCurrentBillAmount(creditCard.getCurrentBillAmount().subtract(oldAmount).add(newAmount));

        var newCategory = transaction.getCategory();
        if (request.categoryId() != null && !request.categoryId().equals(transaction.getCategory().getId())) {
            newCategory = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            validateCategoryOwnership(newCategory, userId);
            validateCategoryTypeMatch(TransactionType.EXPENSE, newCategory.getType());
        }

        if (request.name() != null) transaction.setName(request.name());
        if (request.amount() != null) transaction.setAmount(request.amount());
        if (request.date() != null) transaction.setDate(request.date());
        if (request.isRecurring() != null) {
            transaction.setIsRecurring(request.isRecurring());
            transaction.setFrequency(request.isRecurring() && request.frequency() != null ? request.frequency() : null);
        }
        transaction.setCategory(newCategory);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID transactionId, UUID userId) {
        var transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new AccountNotOwnedException("Você não tem permissão para acessar esta transação");
        }

        if (transaction.getCreditCard() != null) {
            var creditCard = transaction.getCreditCard();
            var bill = transaction.getCreditCardBill();
            if (bill != null) {
                var newBillTotal = bill.getTotalAmount().subtract(transaction.getAmount()).max(BigDecimal.ZERO);
                bill.setTotalAmount(newBillTotal);
            }
            var newCurrentAmount = creditCard.getCurrentBillAmount().subtract(transaction.getAmount()).max(BigDecimal.ZERO);
            creditCard.setCurrentBillAmount(newCurrentAmount);
        } else {
            var bankAccount = bankAccountRepository.findByIdForUpdate(transaction.getBankAccount().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));
            revertAccountBalance(bankAccount, transaction.getAmount(), transaction.getType());
            bankAccountRepository.saveAndFlush(bankAccount);
        }

        var goal = transaction.getGoal();
        if (goal != null) {
            revertGoalAmount(goal, transaction.getAmount(), transaction.getType());
            savingsGoalRepository.saveAndFlush(goal);
        }

        transactionRepository.delete(transaction);
    }

    private void validateAccountOwnership(BankAccount account, UUID userId) {
        if (!account.getUser().getId().equals(userId)) {
            throw new AccountNotOwnedException("Você não tem permissão para acessar esta conta");
        }
    }

    private void validateCategoryOwnership(Category category, UUID userId) {
        if (!category.getUser().getId().equals(userId)) {
            throw new AccountNotOwnedException("Você não tem permissão para acessar esta categoria");
        }
    }

    private void validateCategoryTypeMatch(TransactionType transactionType, TransactionType categoryType) {
        if (!transactionType.equals(categoryType)) {
            throw new CategoryTypeMismatchException(
                    "O tipo da transação (" + transactionType + ") não corresponde ao tipo da categoria (" + categoryType + ")"
            );
        }
    }

    private void updateAccountBalance(BankAccount account, BigDecimal amount, TransactionType type) {
        BigDecimal newBalance;
        if (type == TransactionType.INCOME) {
            newBalance = account.getCurrentBalance().add(amount);
            account.setCurrentBalance(newBalance);
        } else {
            newBalance = account.getCurrentBalance().subtract(amount);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientBalanceException("Saldo insuficiente na conta");
            }
            account.setCurrentBalance(newBalance);
        }
    }

    private void revertAccountBalance(BankAccount account, BigDecimal amount, TransactionType type) {
        if (type == TransactionType.INCOME) {
            account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
        } else {
            account.setCurrentBalance(account.getCurrentBalance().add(amount));
        }
    }

    public MonthlySummaryResponse getMonthlySummary(UUID userId, int year, int month) {
        var startDate = LocalDate.of(year, month, 1);
        var endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        var totalExpense = transactionRepository.sumTotalExpensesExcludingGoalsByUserIdAndDateRange(
                userId, startDate, endDate
        );
        if (totalExpense == null) {
            totalExpense = BigDecimal.ZERO;
        }

        var byCategoryRaw = transactionRepository.sumExpensesByCategoryExcludingGoalsForDateRange(userId, startDate, endDate);
        var byCategory = byCategoryRaw.stream()
                .map(row -> new CategoryExpenseItem(
                        (UUID) row[0],
                        (String) row[1],
                        (BigDecimal) row[2]
                ))
                .toList();

        return new MonthlySummaryResponse(totalExpense, byCategory);
    }

    public void revertGoalAmount(SavingsGoal goal, BigDecimal amount, TransactionType type) {
        if (type == TransactionType.EXPENSE) {
            goal.setCurrentAmount(goal.getCurrentAmount().subtract(amount).max(BigDecimal.ZERO));
        } else {
            goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        }
        if (goal.getStatus() == GoalStatus.COMPLETED
                && goal.getCurrentAmount().compareTo(goal.getTargetAmount()) < 0) {
            goal.setStatus(GoalStatus.ACTIVE);
        }
    }

    private void applyGoalAmount(SavingsGoal goal, BigDecimal amount, TransactionType type) {
        if (type == TransactionType.INCOME) {
            goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        } else {
            goal.setCurrentAmount(goal.getCurrentAmount().subtract(amount).max(BigDecimal.ZERO));
        }
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        }
    }

    /** Transação filha de recorrência; {@code REQUIRES_NEW} isola falhas por data. */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Transaction createRecurringChild(Transaction parent, LocalDate targetDate) {
        if (parent.getBankAccount() == null) {
            return null;
        }
        planLimitService.checkFreePlanTransactionLimit(parent.getUser().getId());

        var child = Transaction.builder()
                .user(parent.getUser())
                .bankAccount(parent.getBankAccount())
                .category(parent.getCategory())
                .name(parent.getName())
                .amount(parent.getAmount())
                .date(targetDate)
                .type(parent.getType())
                .isRecurring(false)
                .frequency(null)
                .parentTransaction(parent)
                .build();

        var saved = transactionRepository.save(child);
        var account = bankAccountRepository.findByIdForUpdate(parent.getBankAccount().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));
        updateAccountBalance(account, parent.getAmount(), parent.getType());
        bankAccountRepository.save(account);
        return saved;
    }
}
