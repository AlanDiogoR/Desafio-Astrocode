package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    
    @Query("SELECT ba FROM BankAccount ba WHERE ba.user.id = :userId")
    List<BankAccount> findByUserId(@Param("userId") UUID userId);
}
