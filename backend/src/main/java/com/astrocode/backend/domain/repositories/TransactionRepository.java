package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.Transaction;
import com.astrocode.backend.domain.model.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.bankAccount LEFT JOIN FETCH t.creditCard LEFT JOIN FETCH t.creditCardBill JOIN FETCH t.category WHERE t.user.id = :userId ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT DISTINCT t FROM Transaction t LEFT JOIN t.bankAccount LEFT JOIN t.creditCard LEFT JOIN t.creditCardBill JOIN t.category WHERE t.user.id = :userId ORDER BY t.date DESC, t.createdAt DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.user.id = :userId")
    Page<Transaction> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.bankAccount LEFT JOIN FETCH t.creditCard LEFT JOIN FETCH t.creditCardBill JOIN FETCH t.category WHERE t.user.id = :userId AND t.date BETWEEN :startDate AND :endDate ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndDateBetween(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.bankAccount LEFT JOIN FETCH t.creditCard LEFT JOIN FETCH t.creditCardBill JOIN FETCH t.category WHERE t.user.id = :userId AND t.date >= :startDate AND t.date <= :endDate ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndDateYearAndDateMonth(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = "SELECT DISTINCT t FROM Transaction t LEFT JOIN t.bankAccount LEFT JOIN t.creditCard LEFT JOIN t.creditCardBill JOIN t.category WHERE t.user.id = :userId AND t.date >= :startDate AND t.date <= :endDate ORDER BY t.date DESC, t.createdAt DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.user.id = :userId AND t.date >= :startDate AND t.date <= :endDate")
    Page<Transaction> findByUserIdAndDateYearAndDateMonth(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("SELECT t FROM Transaction t JOIN FETCH t.bankAccount JOIN FETCH t.category WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndBankAccountId(
            @Param("userId") UUID userId,
            @Param("bankAccountId") UUID bankAccountId
    );

    @Query(value = "SELECT t FROM Transaction t JOIN t.bankAccount JOIN t.category WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId ORDER BY t.date DESC, t.createdAt DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId")
    Page<Transaction> findByUserIdAndBankAccountId(
            @Param("userId") UUID userId,
            @Param("bankAccountId") UUID bankAccountId,
            Pageable pageable
    );

    @Query("SELECT t FROM Transaction t JOIN FETCH t.bankAccount JOIN FETCH t.category WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId AND t.date >= :startDate AND t.date <= :endDate ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndBankAccountIdAndDateYearAndDateMonth(
            @Param("userId") UUID userId,
            @Param("bankAccountId") UUID bankAccountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = "SELECT t FROM Transaction t JOIN t.bankAccount JOIN t.category WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId AND t.date >= :startDate AND t.date <= :endDate ORDER BY t.date DESC, t.createdAt DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId AND t.date >= :startDate AND t.date <= :endDate")
    Page<Transaction> findByUserIdAndBankAccountIdAndDateYearAndDateMonth(
            @Param("userId") UUID userId,
            @Param("bankAccountId") UUID bankAccountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.bankAccount LEFT JOIN FETCH t.creditCard LEFT JOIN FETCH t.creditCardBill JOIN FETCH t.category WHERE t.user.id = :userId AND t.type = :type ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndType(
            @Param("userId") UUID userId,
            @Param("type") TransactionType type
    );

    @Query(value = "SELECT DISTINCT t FROM Transaction t LEFT JOIN t.bankAccount LEFT JOIN t.creditCard LEFT JOIN t.creditCardBill JOIN t.category WHERE t.user.id = :userId AND t.type = :type ORDER BY t.date DESC, t.createdAt DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type")
    Page<Transaction> findByUserIdAndType(
            @Param("userId") UUID userId,
            @Param("type") TransactionType type,
            Pageable pageable
    );

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.bankAccount LEFT JOIN FETCH t.creditCard LEFT JOIN FETCH t.creditCardBill JOIN FETCH t.category WHERE t.user.id = :userId AND t.date >= :startDate AND t.date <= :endDate AND t.type = :type ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndDateYearAndDateMonthAndType(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") TransactionType type
    );

    @Query(value = "SELECT DISTINCT t FROM Transaction t LEFT JOIN t.bankAccount LEFT JOIN t.creditCard LEFT JOIN t.creditCardBill JOIN t.category WHERE t.user.id = :userId AND t.date >= :startDate AND t.date <= :endDate AND t.type = :type ORDER BY t.date DESC, t.createdAt DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.user.id = :userId AND t.date >= :startDate AND t.date <= :endDate AND t.type = :type")
    Page<Transaction> findByUserIdAndDateYearAndDateMonthAndType(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") TransactionType type,
            Pageable pageable
    );

    @Query("SELECT t FROM Transaction t JOIN FETCH t.bankAccount JOIN FETCH t.category WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId AND t.type = :type ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndBankAccountIdAndType(
            @Param("userId") UUID userId,
            @Param("bankAccountId") UUID bankAccountId,
            @Param("type") TransactionType type
    );

    @Query(value = "SELECT t FROM Transaction t JOIN t.bankAccount JOIN t.category WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId AND t.type = :type ORDER BY t.date DESC, t.createdAt DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId AND t.type = :type")
    Page<Transaction> findByUserIdAndBankAccountIdAndType(
            @Param("userId") UUID userId,
            @Param("bankAccountId") UUID bankAccountId,
            @Param("type") TransactionType type,
            Pageable pageable
    );

    @Query("SELECT t FROM Transaction t JOIN FETCH t.bankAccount JOIN FETCH t.category WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId AND t.date >= :startDate AND t.date <= :endDate AND t.type = :type ORDER BY t.date DESC, t.createdAt DESC")
    List<Transaction> findByUserIdAndBankAccountIdAndDateYearAndDateMonthAndType(
            @Param("userId") UUID userId,
            @Param("bankAccountId") UUID bankAccountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") TransactionType type
    );

    @Query(value = "SELECT t FROM Transaction t JOIN t.bankAccount JOIN t.category WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId AND t.date >= :startDate AND t.date <= :endDate AND t.type = :type ORDER BY t.date DESC, t.createdAt DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.user.id = :userId AND t.bankAccount.id = :bankAccountId AND t.date >= :startDate AND t.date <= :endDate AND t.type = :type")
    Page<Transaction> findByUserIdAndBankAccountIdAndDateYearAndDateMonthAndType(
            @Param("userId") UUID userId,
            @Param("bankAccountId") UUID bankAccountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") TransactionType type,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.date >= :startDate AND t.date <= :endDate")
    BigDecimal sumTotalByUserIdAndTypeAndDateRange(
            @Param("userId") UUID userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = 'EXPENSE' AND t.goal IS NULL AND t.date >= :startDate AND t.date <= :endDate")
    BigDecimal sumTotalExpensesExcludingGoalsByUserIdAndDateRange(
            @Param("userId") UUID userId,
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

    @Query("SELECT t.category.id, t.category.name, SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.type = 'EXPENSE' AND t.goal IS NULL AND t.date >= :startDate AND t.date <= :endDate GROUP BY t.category.id, t.category.name ORDER BY SUM(t.amount) DESC")
    List<Object[]> sumExpensesByCategoryExcludingGoalsForDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT t FROM Transaction t WHERE t.isRecurring = true AND t.parentTransaction IS NULL")
    List<Transaction> findParentRecurringTransactions();

    @Query(value = "SELECT t FROM Transaction t WHERE t.isRecurring = true AND t.parentTransaction IS NULL")
    Page<Transaction> findParentRecurringTransactions(Pageable pageable);

    @Query("SELECT COUNT(t) > 0 FROM Transaction t WHERE t.parentTransaction.id = :parentId AND t.date >= :startDate AND t.date <= :endDate")
    boolean existsChildForParentInDateRange(
            @Param("parentId") UUID parentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.user.id = :userId AND t.date >= :startDate AND t.date <= :endDate")
    long countByUserIdAndDateBetween(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
