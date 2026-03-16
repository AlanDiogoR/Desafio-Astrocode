package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.Subscription;
import com.astrocode.backend.domain.model.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    @Query("SELECT s FROM Subscription s JOIN FETCH s.user WHERE s.user.id = :userId")
    Optional<Subscription> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT s FROM Subscription s WHERE s.status = :status AND s.expiresAt < :now")
    List<Subscription> findByStatusAndExpiresAtBefore(
            @Param("status") SubscriptionStatus status,
            @Param("now") OffsetDateTime now
    );

    Optional<Subscription> findByMpExternalReference(String mpExternalReference);
}
