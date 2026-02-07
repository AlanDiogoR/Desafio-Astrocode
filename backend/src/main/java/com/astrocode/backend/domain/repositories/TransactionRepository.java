package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
