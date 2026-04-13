package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.subscription WHERE u.id = :userId")
    Optional<User> findByIdWithSubscription(@Param("userId") UUID userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.subscription WHERE u.email = :email")
    Optional<User> findByEmailWithSubscription(@Param("email") String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmailVerificationToken(String token);

    Optional<User> findByRefreshTokenHash(String refreshTokenHash);

    Optional<User> findByWhatsappPhone(String whatsappPhone);

    Optional<User> findByMetaUserId(String metaUserId);

    @Query("""
            SELECT u FROM User u
            WHERE u.marketingEmailsOptOut = false
            AND (
              (u.lastLoginAt IS NOT NULL AND u.lastLoginAt < :cutoff)
              OR (u.lastLoginAt IS NULL AND u.createdAt < :cutoff)
            )
            AND (u.lastReactivationEmailAt IS NULL OR u.lastReactivationEmailAt < :reactivationCooldown)
            """)
    List<User> findInactiveForReactivation(@Param("cutoff") OffsetDateTime cutoff,
                                           @Param("reactivationCooldown") OffsetDateTime reactivationCooldown);
}
