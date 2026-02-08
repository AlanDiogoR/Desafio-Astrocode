package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.TransactionRequest;
import com.astrocode.backend.api.dto.TransactionUpdateRequest;
import com.astrocode.backend.domain.entities.BankAccount;
import com.astrocode.backend.domain.entities.Category;
import com.astrocode.backend.domain.entities.Transaction;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.AccountNotOwnedException;
import com.astrocode.backend.domain.exceptions.CategoryTypeMismatchException;
import com.astrocode.backend.domain.exceptions.InsufficientBalanceException;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.repositories.BankAccountRepository;
import com.astrocode.backend.domain.repositories.CategoryRepository;
import com.astrocode.backend.domain.repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            BankAccountRepository bankAccountRepository,
            CategoryRepository categoryRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Transaction create(TransactionRequest request, UUID userId) {
        var bankAccount = bankAccountRepository.findById(request.bankAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));

        validateAccountOwnership(bankAccount, userId);

        var category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        validateCategoryOwnership(category, userId);
        validateCategoryTypeMatch(request.type(), category.getType());

        var transaction = Transaction.builder()
                .user(bankAccount.getUser())
                .bankAccount(bankAccount)
                .category(category)
                .name(request.name())
                .amount(request.amount())
                .date(request.date())
                .type(request.type())
                .build();

        var savedTransaction = transactionRepository.save(transaction);
        updateAccountBalance(bankAccount, request.amount(), request.type());
        bankAccountRepository.save(bankAccount);

        return savedTransaction;
    }

    public List<Transaction> findAllByUserId(UUID userId, Integer year, Integer month, UUID bankAccountId, TransactionType type) {
        if (bankAccountId != null) {
            var bankAccount = bankAccountRepository.findById(bankAccountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));
            validateAccountOwnership(bankAccount, userId);

            if (year != null && month != null) {
                var startDate = LocalDate.of(year, month, 1);
                var endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                if (type != null) {
                    return transactionRepository.findByUserIdAndBankAccountIdAndDateYearAndDateMonthAndType(userId, bankAccountId, startDate, endDate, type);
                }
                return transactionRepository.findByUserIdAndBankAccountIdAndDateYearAndDateMonth(userId, bankAccountId, startDate, endDate);
            }
            if (type != null) {
                return transactionRepository.findByUserIdAndBankAccountIdAndType(userId, bankAccountId, type);
            }
            return transactionRepository.findByUserIdAndBankAccountId(userId, bankAccountId);
        }

        if (year != null && month != null) {
            var startDate = LocalDate.of(year, month, 1);
            var endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            if (type != null) {
                return transactionRepository.findByUserIdAndDateYearAndDateMonthAndType(userId, startDate, endDate, type);
            }
            return transactionRepository.findByUserIdAndDateYearAndDateMonth(userId, startDate, endDate);
        }

        if (type != null) {
            return transactionRepository.findByUserIdAndType(userId, type);
        }

        return transactionRepository.findByUserId(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Transaction update(UUID transactionId, TransactionUpdateRequest request, UUID userId) {
        var transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new AccountNotOwnedException("Você não tem permissão para acessar esta transação");
        }

        var oldBankAccount = transaction.getBankAccount();
        var oldAmount = transaction.getAmount();
        var oldType = transaction.getType();

        revertAccountBalance(oldBankAccount, oldAmount, oldType);

        var newBankAccount = oldBankAccount;
        if (request.bankAccountId() != null && !request.bankAccountId().equals(oldBankAccount.getId())) {
            newBankAccount = bankAccountRepository.findById(request.bankAccountId())
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

        if (request.name() != null) {
            transaction.setName(request.name());
        }
        if (request.amount() != null) {
            transaction.setAmount(request.amount());
        }
        if (request.date() != null) {
            transaction.setDate(request.date());
        }
        transaction.setType(newType);
        transaction.setBankAccount(newBankAccount);
        transaction.setCategory(newCategory);

        var newAmount = request.amount() != null ? request.amount() : oldAmount;
        updateAccountBalance(newBankAccount, newAmount, newType);

        if (!oldBankAccount.getId().equals(newBankAccount.getId())) {
            bankAccountRepository.saveAndFlush(oldBankAccount);
        }
        bankAccountRepository.saveAndFlush(newBankAccount);

        return transactionRepository.save(transaction);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID transactionId, UUID userId) {
        var transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new AccountNotOwnedException("Você não tem permissão para acessar esta transação");
        }

        var bankAccount = transaction.getBankAccount();
        revertAccountBalance(bankAccount, transaction.getAmount(), transaction.getType());
        bankAccountRepository.saveAndFlush(bankAccount);

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
            newBalance = account.getInitialBalance().add(amount);
            account.setInitialBalance(newBalance);
        } else {
            newBalance = account.getInitialBalance().subtract(amount);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientBalanceException("Saldo insuficiente na conta");
            }
            account.setInitialBalance(newBalance);
        }
    }

    private void revertAccountBalance(BankAccount account, BigDecimal amount, TransactionType type) {
        if (type == TransactionType.INCOME) {
            account.setInitialBalance(account.getInitialBalance().subtract(amount));
        } else {
            account.setInitialBalance(account.getInitialBalance().add(amount));
        }
    }
}
