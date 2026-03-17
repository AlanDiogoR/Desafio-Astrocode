package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.CreditCardBill;
import com.astrocode.backend.domain.model.enums.BillStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditCardBillRepository extends JpaRepository<CreditCardBill, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM CreditCardBill b WHERE b.id = :id")
    Optional<CreditCardBill> findByIdForUpdate(@Param("id") UUID id);

    @Query("SELECT b FROM CreditCardBill b WHERE b.creditCard.id = :creditCardId ORDER BY b.year DESC, b.month DESC")
    List<CreditCardBill> findByCreditCardId(@Param("creditCardId") UUID creditCardId);

    @Query("SELECT b FROM CreditCardBill b WHERE b.creditCard.id = :creditCardId AND b.month = :month AND b.year = :year")
    Optional<CreditCardBill> findByCreditCardIdAndMonthAndYear(
            @Param("creditCardId") UUID creditCardId,
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    @Query("SELECT b FROM CreditCardBill b WHERE b.creditCard.user.id = :userId AND b.creditCard.id = :creditCardId AND b.month = :month AND b.year = :year")
    Optional<CreditCardBill> findByUserIdAndCreditCardIdAndMonthAndYear(
            @Param("userId") UUID userId,
            @Param("creditCardId") UUID creditCardId,
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    @Query("SELECT b FROM CreditCardBill b WHERE b.creditCard.user.id = :userId AND b.creditCard.id = :creditCardId ORDER BY b.year DESC, b.month DESC")
    List<CreditCardBill> findByUserIdAndCreditCardId(
            @Param("userId") UUID userId,
            @Param("creditCardId") UUID creditCardId
    );

    Optional<CreditCardBill> findFirstByCreditCardIdAndStatusOrderByYearDescMonthDesc(
            UUID creditCardId,
            BillStatus status
    );
}
