package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, UUID> {

    @Query("SELECT cc FROM CreditCard cc WHERE cc.user.id = :userId ORDER BY cc.name")
    List<CreditCard> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT cc FROM CreditCard cc WHERE cc.user.id = :userId AND LOWER(cc.name) = LOWER(:name)")
    Optional<CreditCard> findByUserIdAndNameIgnoreCase(@Param("userId") UUID userId, @Param("name") String name);

    @Query("SELECT cc FROM CreditCard cc WHERE cc.user.id = :userId AND cc.id = :creditCardId")
    Optional<CreditCard> findByUserIdAndId(@Param("userId") UUID userId, @Param("creditCardId") UUID creditCardId);
}
