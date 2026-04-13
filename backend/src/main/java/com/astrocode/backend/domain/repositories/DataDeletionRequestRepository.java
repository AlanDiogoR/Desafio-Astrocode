package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.DataDeletionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DataDeletionRequestRepository extends JpaRepository<DataDeletionRequest, UUID> {

    Optional<DataDeletionRequest> findByConfirmationCode(String confirmationCode);

    List<DataDeletionRequest> findByStatusAndScheduledForBefore(String status, OffsetDateTime before);
}
