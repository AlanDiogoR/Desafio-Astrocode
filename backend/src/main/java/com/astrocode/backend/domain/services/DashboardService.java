package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.DashboardResponse;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.repositories.BankAccountRepository;
import com.astrocode.backend.domain.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class DashboardService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    public DashboardService(
            BankAccountRepository bankAccountRepository,
            TransactionRepository transactionRepository
    ) {
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
    }

    public DashboardResponse getDashboardData(UUID userId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        BigDecimal totalBalance = bankAccountRepository.sumTotalBalanceByUserId(userId);
        if (totalBalance == null) {
            totalBalance = BigDecimal.ZERO;
        }

        BigDecimal totalIncome = transactionRepository.sumTotalByUserIdAndTypeAndDateRange(
                userId, TransactionType.INCOME, startDate, endDate
        );
        if (totalIncome == null) {
            totalIncome = BigDecimal.ZERO;
        }

        BigDecimal totalExpense = transactionRepository.sumTotalByUserIdAndTypeAndDateRange(
                userId, TransactionType.EXPENSE, startDate, endDate
        );
        if (totalExpense == null) {
            totalExpense = BigDecimal.ZERO;
        }

        return new DashboardResponse(totalBalance, totalIncome, totalExpense);
    }
}
