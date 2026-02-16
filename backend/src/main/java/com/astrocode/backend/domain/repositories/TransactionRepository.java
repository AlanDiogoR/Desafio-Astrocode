package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.Transaction;
import com.astrocode.backend.domain.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.date BETWEEN :startDate AND :endDate ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndDateBetween(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.date >= :startDate AND t.date <= :endDate ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndDateYearAndDateMonth(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndBankAccountId(
            @Param("userId") UUID userId,
            @Param("bankAccountId") UUID bankAccountId
    );

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId AND t.date >= :startDate AND t.date <= :endDate ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndBankAccountIdAndDateYearAndDateMonth(
            @Param("userId") UUID userId,
            @Param("bankAccountId") UUID bankAccountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.type = :type ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndType(
            @Param("userId") UUID userId,
            @Param("type") TransactionType type
    );

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.date >= :startDate AND t.date <= :endDate AND t.type = :type ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndDateYearAndDateMonthAndType(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") TransactionType type
    );

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId AND t.type = :type ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndBankAccountIdAndType(
            @Param("userId") UUID userId,
            @Param("bankAccountId") UUID bankAccountId,
            @Param("type") TransactionType type
    );

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId AND t.date >= :startDate AND t.date <= :endDate AND t.type = :type ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndBankAccountIdAndDateYearAndDateMonthAndType(
            @Param("userId") UUID userId,
            @Param("bankAccountId") UUID bankAccountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") TransactionType type
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.date >= :startDate AND t.date <= :endDate")
    BigDecimal sumTotalByUserIdAndTypeAndDateRange(
            @Param("userId") UUID userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT t.category.id, t.category.name, SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.date >= :startDate AND t.date <= :endDate GROUP BY t.category.id, t.category.name ORDER BY SUM(t.amount) DESC")
    List<Object[]> sumExpensesByCategoryForDateRange(
            @Param("userId") UUID userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
