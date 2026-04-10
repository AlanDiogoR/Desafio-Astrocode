package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.OpenFinanceWaitlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OpenFinanceWaitlistRepository extends JpaRepository<OpenFinanceWaitlistEntry, UUID> {

    Optional<OpenFinanceWaitlistEntry> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
