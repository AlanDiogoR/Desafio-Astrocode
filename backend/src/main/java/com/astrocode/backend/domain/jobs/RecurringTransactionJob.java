package com.astrocode.backend.domain.jobs;

import com.astrocode.backend.domain.entities.Transaction;
import com.astrocode.backend.domain.model.enums.RecurrenceFrequency;
import com.astrocode.backend.domain.repositories.TransactionRepository;
import com.astrocode.backend.domain.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Job que executa diariamente para gerar transações filhas a partir de transações pai recorrentes.
 * Roda às 00:05 AM e cria transações para o mês/ano atual que ainda não existem.
 */
@Component
public class RecurringTransactionJob {

    private static final Logger log = LoggerFactory.getLogger(RecurringTransactionJob.class);

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public RecurringTransactionJob(
            TransactionRepository transactionRepository,
            TransactionService transactionService
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    @Scheduled(cron = "0 5 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void generateRecurringTransactions() {
        log.info("Iniciando job de geração de transações recorrentes");

        var parents = transactionRepository.findParentRecurringTransactions();
        if (parents.isEmpty()) {
            log.debug("Nenhuma transação pai recorrente encontrada");
            return;
        }

        var today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        int generated = 0;

        for (Transaction parent : parents) {
            try {
                var frequency = parent.getFrequency() != null ? parent.getFrequency() : RecurrenceFrequency.MONTHLY;
                LocalDate targetDate;
                LocalDate rangeStart;
                LocalDate rangeEnd;

                if (frequency == RecurrenceFrequency.YEARLY) {
                    var parentDate = parent.getDate();
                    int day = Math.min(parentDate.getDayOfMonth(),
                            YearMonth.of(currentYear, parentDate.getMonthValue()).lengthOfMonth());
                    targetDate = LocalDate.of(currentYear, parentDate.getMonthValue(), day);
                    rangeStart = LocalDate.of(currentYear, 1, 1);
                    rangeEnd = LocalDate.of(currentYear, 12, 31);
                } else {
                    int day = Math.min(parent.getDate().getDayOfMonth(),
                            YearMonth.of(currentYear, currentMonth).lengthOfMonth());
                    targetDate = LocalDate.of(currentYear, currentMonth, day);
                    rangeStart = LocalDate.of(currentYear, currentMonth, 1);
                    rangeEnd = rangeStart.withDayOfMonth(rangeStart.lengthOfMonth());
                }

                var exists = transactionRepository.existsChildForParentInDateRange(
                        parent.getId(), rangeStart, rangeEnd
                );

                if (!exists) {
                    transactionService.createRecurringChild(parent, targetDate);
                    generated++;
                    log.debug("Transação recorrente gerada: {} (pai: {})", targetDate, parent.getId());
                }
            } catch (Exception e) {
                log.warn("Erro ao gerar transação recorrente para pai {}: {}", parent.getId(), e.getMessage());
            }
        }

        log.info("Job concluído. {} transação(ões) recorrente(s) gerada(s)", generated);
    }
}
