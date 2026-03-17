package com.astrocode.backend.domain.services;

import com.astrocode.backend.domain.exceptions.PlanUpgradeRequiredException;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import com.astrocode.backend.domain.repositories.TransactionRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class PlanLimitService {

    private static final int FREE_PLAN_TRANSACTION_LIMIT_PER_MONTH = 30;

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public PlanLimitService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
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
                    "Você atingiu o limite de " + FREE_PLAN_TRANSACTION_LIMIT_PER_MONTH + " transações no mês no plano gratuito. Faça upgrade para continuar.",
                    "transactions");
        }
    }
}
