package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, UUID> {

    Optional<PasswordResetCode> findFirstByEmailOrderByCreatedAtDesc(String email);

    boolean existsByEmailAndCreatedAtAfter(String email, OffsetDateTime after);

    void deleteByEmail(String email);
}
