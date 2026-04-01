package com.astrocode.backend.domain.services;

import com.astrocode.backend.domain.exceptions.PlanUpgradeRequiredException;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import com.astrocode.backend.domain.repositories.BankAccountRepository;
import com.astrocode.backend.domain.repositories.SavingsGoalRepository;
import com.astrocode.backend.domain.repositories.TransactionRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class PlanLimitService {

    public static final int FREE_PLAN_TRANSACTION_LIMIT_PER_MONTH = 30;
    public static final int FREE_PLAN_ACCOUNT_LIMIT = 2;
    public static final int FREE_PLAN_ACTIVE_GOAL_LIMIT = 2;

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final SavingsGoalRepository savingsGoalRepository;

    public PlanLimitService(UserRepository userRepository,
                            TransactionRepository transactionRepository,
                            BankAccountRepository bankAccountRepository,
                            SavingsGoalRepository savingsGoalRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.savingsGoalRepository = savingsGoalRepository;
    }

    public void checkFreePlanTransactionLimit(UUID userId) {
        var user = userRepository.findByIdWithSubscription(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        if (user.get().isPro()) {
            return;
        }
        var now = LocalDate.now();
        var startOfMonth = now.withDayOfMonth(1);
        var endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        long count = transactionRepository.countByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);
        if (count >= FREE_PLAN_TRANSACTION_LIMIT_PER_MONTH) {
            throw new PlanUpgradeRequiredException(
                    "Seu controle financeiro está evoluindo! Você atingiu o limite de "
                            + FREE_PLAN_TRANSACTION_LIMIT_PER_MONTH
                            + " transações do plano Free. Libere transações ilimitadas agora no Grivy Pro e nunca mais perca um lançamento.",
                    "transactions");
        }
    }

    public void checkFreePlanBankAccountLimit(UUID userId) {
        var user = userRepository.findByIdWithSubscription(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        if (user.get().isPro()) {
            return;
        }
        long count = bankAccountRepository.countByUser_Id(userId);
        if (count >= FREE_PLAN_ACCOUNT_LIMIT) {
            throw new PlanUpgradeRequiredException(
                    "Seu controle financeiro está evoluindo! Você atingiu o limite de "
                            + FREE_PLAN_ACCOUNT_LIMIT
                            + " contas do plano Free. Faça upgrade para o Grivy Pro e organize todas as suas contas em um só lugar.",
                    "accounts");
        }
    }

    public void checkFreePlanActiveGoalLimit(UUID userId) {
        var user = userRepository.findByIdWithSubscription(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        if (user.get().isPro()) {
            return;
        }
        long activeCount = savingsGoalRepository.countActiveByUserId(userId);
        if (activeCount >= FREE_PLAN_ACTIVE_GOAL_LIMIT) {
            throw new PlanUpgradeRequiredException(
                    "Seu controle financeiro está evoluindo! Você atingiu o limite de "
                            + FREE_PLAN_ACTIVE_GOAL_LIMIT
                            + " metas ativas do plano Free. Libere metas ilimitadas no Grivy Pro e acompanhe todos os seus sonhos.",
                    "goals");
        }
    }
}
