package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.account.BankAccountRequest;
import com.astrocode.backend.domain.entities.BankAccount;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.DuplicateAccountNameException;
import com.astrocode.backend.domain.exceptions.ResourceAccessDeniedException;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import com.astrocode.backend.domain.repositories.BankAccountRepository;
import com.astrocode.backend.domain.repositories.SavingsGoalRepository;
import com.astrocode.backend.domain.repositories.TransactionRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.astrocode.backend.domain.entities.SavingsGoal;
import com.astrocode.backend.domain.model.enums.TransactionType;

@Service
@Transactional(readOnly = true)
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final SavingsGoalRepository savingsGoalRepository;
    private final TransactionRepository transactionRepository;
    private final PlanLimitService planLimitService;

    public BankAccountService(BankAccountRepository bankAccountRepository,
                              UserRepository userRepository,
                              TransactionService transactionService,
                              SavingsGoalRepository savingsGoalRepository,
                              TransactionRepository transactionRepository,
                              PlanLimitService planLimitService) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.savingsGoalRepository = savingsGoalRepository;
        this.transactionRepository = transactionRepository;
        this.planLimitService = planLimitService;
    }

    @Transactional
    public BankAccount create(BankAccountRequest request, UUID userId) {
        var user = userRepository.findByIdWithSubscription(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        planLimitService.checkFreePlanBankAccountLimit(userId);

        var existing = bankAccountRepository.findByUserIdAndNameIgnoreCase(userId, request.name().trim());
        if (!existing.isEmpty()) {
            throw new DuplicateAccountNameException(request.name().trim());
        }

        var bankAccount = BankAccount.builder()
                .user(user)
                .name(request.name())
                .initialBalance(request.initialBalance())
                .currentBalance(request.initialBalance())
                .type(request.type())
                .color(request.color())
                .build();

        return bankAccountRepository.save(bankAccount);
    }

    public List<BankAccount> findAllByUserId(UUID userId) {
        return bankAccountRepository.findByUserId(userId);
    }

    @Transactional
    public BankAccount update(UUID accountId, BankAccountRequest request, User user) {
        var account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("Você não tem permissão para acessar esta conta");
        }

        var existing = bankAccountRepository.findByUserIdAndNameIgnoreCase(user.getId(), request.name().trim());
        boolean duplicateName = existing.stream()
                .anyMatch(a -> !a.getId().equals(accountId));
        if (duplicateName) {
            throw new DuplicateAccountNameException(request.name().trim());
        }

        var balanceDiff = request.initialBalance().subtract(account.getInitialBalance());
        account.setCurrentBalance(account.getCurrentBalance().add(balanceDiff));
        account.setInitialBalance(request.initialBalance());
        account.setName(request.name());
        account.setType(request.type());
        account.setColor(request.color());

        return bankAccountRepository.save(account);
    }

    @Transactional
    public void delete(UUID accountId, User user) {
        var account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("Você não tem permissão para acessar esta conta");
        }

        var rows = transactionRepository.findGoalImpactRowsByBankAccountId(account.getId());
        if (!rows.isEmpty()) {
            var goalIds = rows.stream().map(r -> (UUID) r[0]).distinct().toList();
            var goalsById = savingsGoalRepository.findAllById(goalIds).stream()
                    .collect(Collectors.toMap(SavingsGoal::getId, g -> g));
            for (Object[] row : rows) {
                UUID goalId = (UUID) row[0];
                var type = (TransactionType) row[1];
                var amount = (BigDecimal) row[2];
                transactionService.revertGoalAmount(goalsById.get(goalId), amount, type);
            }
            savingsGoalRepository.saveAll(goalsById.values());
        }

        bankAccountRepository.delete(account);
    }
}
