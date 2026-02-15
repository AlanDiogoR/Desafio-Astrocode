package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.goal.SavingsGoalContributeRequest;
import com.astrocode.backend.api.dto.goal.SavingsGoalRequest;
import com.astrocode.backend.api.dto.goal.SavingsGoalWithdrawRequest;
import com.astrocode.backend.domain.entities.BankAccount;
import com.astrocode.backend.domain.entities.Category;
import com.astrocode.backend.domain.entities.SavingsGoal;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.ResourceAccessDeniedException;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import com.astrocode.backend.domain.model.enums.GoalStatus;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.repositories.BankAccountRepository;
import com.astrocode.backend.domain.repositories.CategoryRepository;
import com.astrocode.backend.domain.repositories.SavingsGoalRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionService transactionService;

    public SavingsGoalService(
            SavingsGoalRepository savingsGoalRepository,
            UserRepository userRepository,
            BankAccountRepository bankAccountRepository,
            CategoryRepository categoryRepository,
            TransactionService transactionService
    ) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionService = transactionService;
    }

    @Transactional
    public SavingsGoal create(SavingsGoalRequest request, UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (request.targetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor alvo deve ser maior que zero");
        }

        var savingsGoal = SavingsGoal.builder()
                .user(user)
                .name(request.name())
                .targetAmount(request.targetAmount())
                .currentAmount(BigDecimal.ZERO)
                .startDate(LocalDate.now())
                .endDate(request.endDate())
                .status(GoalStatus.ACTIVE)
                .color(request.color())
                .build();

        return savingsGoalRepository.save(savingsGoal);
    }

    public List<SavingsGoal> findAllByUserId(UUID userId) {
        return savingsGoalRepository.findByUserId(userId);
    }

    public SavingsGoal findByIdAndUserId(UUID id, UUID userId) {
        var savingsGoal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta de poupança não encontrada"));

        if (!savingsGoal.getUser().getId().equals(userId)) {
            throw new ResourceAccessDeniedException("Você não tem permissão para acessar esta meta");
        }
        if (savingsGoal.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Meta de poupança não encontrada");
        }

        return savingsGoal;
    }

    @Transactional
    public SavingsGoal update(UUID id, SavingsGoalRequest request, User user) {
        var savingsGoal = findByIdAndUserId(id, user.getId());

        if (request.targetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor alvo deve ser maior que zero");
        }

        savingsGoal.setName(request.name());
        savingsGoal.setTargetAmount(request.targetAmount());
        savingsGoal.setEndDate(request.endDate());
        savingsGoal.setColor(request.color());

        return savingsGoalRepository.save(savingsGoal);
    }

    @Transactional(rollbackFor = Exception.class)
    public SavingsGoal contribute(UUID id, SavingsGoalContributeRequest request, User user) {
        var goal = findByIdAndUserId(id, user.getId());
        validateNotCompleted(goal);

        BigDecimal remaining = goal.getTargetAmount().subtract(goal.getCurrentAmount());
        if (request.amount().compareTo(remaining) > 0) {
            throw new IllegalArgumentException("Valor não pode ultrapassar o que falta para atingir a meta");
        }

        var bankAccount = bankAccountRepository.findById(request.bankAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));
        if (!bankAccount.getUser().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("Você não tem permissão para acessar esta conta");
        }

        var expenseCategories = categoryRepository.findByUserIdAndType(user.getId(), TransactionType.EXPENSE);
        if (expenseCategories.isEmpty()) {
            throw new ResourceNotFoundException("Nenhuma categoria de despesa encontrada");
        }
        var category = expenseCategories.getFirst();

        goal.setCurrentAmount(goal.getCurrentAmount().add(request.amount()));
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        }

        transactionService.createGoalTransaction(
                bankAccount,
                category,
                "Aporte na meta: " + goal.getName(),
                request.amount(),
                TransactionType.EXPENSE,
                user,
                goal
        );

        return savingsGoalRepository.save(goal);
    }

    @Transactional(rollbackFor = Exception.class)
    public SavingsGoal withdraw(UUID id, SavingsGoalWithdrawRequest request, User user) {
        var goal = findByIdAndUserId(id, user.getId());

        if (request.amount().compareTo(goal.getCurrentAmount()) > 0) {
            throw new IllegalArgumentException("Valor não pode ser maior que o saldo atual da meta");
        }

        var bankAccount = bankAccountRepository.findById(request.bankAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));
        if (!bankAccount.getUser().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("Você não tem permissão para acessar esta conta");
        }

        var incomeCategories = categoryRepository.findByUserIdAndType(user.getId(), TransactionType.INCOME);
        if (incomeCategories.isEmpty()) {
            throw new ResourceNotFoundException("Nenhuma categoria de receita encontrada");
        }
        var category = incomeCategories.getFirst();

        goal.setCurrentAmount(goal.getCurrentAmount().subtract(request.amount()));
        if (goal.getStatus() == GoalStatus.COMPLETED
                && goal.getCurrentAmount().compareTo(goal.getTargetAmount()) < 0) {
            goal.setStatus(GoalStatus.ACTIVE);
        }

        transactionService.createGoalTransaction(
                bankAccount,
                category,
                "Resgate da meta: " + goal.getName(),
                request.amount(),
                TransactionType.INCOME,
                user,
                goal
        );

        return savingsGoalRepository.save(goal);
    }

    @Transactional
    public void delete(UUID id, User user) {
        var savingsGoal = findByIdAndUserId(id, user.getId());
        savingsGoal.setDeletedAt(OffsetDateTime.now());
        savingsGoalRepository.save(savingsGoal);
    }

    private void validateNotCompleted(SavingsGoal goal) {
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalArgumentException("Não é possível adicionar valor em uma meta já concluída");
        }
    }
}
