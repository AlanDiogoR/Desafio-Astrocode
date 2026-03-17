package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.BankAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BankAccount b WHERE b.id = :id")
    Optional<BankAccount> findByIdForUpdate(@Param("id") UUID id);
    
    @Query("SELECT ba FROM BankAccount ba WHERE ba.user.id = :userId")
    List<BankAccount> findByUserId(@Param("userId") UUID userId);

    long countByUser_Id(UUID userId);


    @Query("SELECT ba FROM BankAccount ba WHERE ba.user.id = :userId AND LOWER(ba.name) = LOWER(:name)")
    List<BankAccount> findByUserIdAndNameIgnoreCase(@Param("userId") UUID userId, @Param("name") String name);

    Optional<BankAccount> findByPluggyAccountId(String pluggyAccountId);

    @Query("SELECT ba FROM BankAccount ba WHERE ba.user.id = :userId AND ba.pluggyItemId = :pluggyItemId")
    List<BankAccount> findByUserIdAndPluggyItemId(@Param("userId") UUID userId, @Param("pluggyItemId") String pluggyItemId);

    @Query("SELECT COALESCE(SUM(b.currentBalance), 0) FROM BankAccount b WHERE b.user.id = :userId")
    BigDecimal sumTotalBalanceByUserId(@Param("userId") UUID userId);
}
