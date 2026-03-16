package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.WebhookProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookProcessedEventRepository extends JpaRepository<WebhookProcessedEvent, java.util.UUID> {

    boolean existsByPaymentIdAndRequestId(Long paymentId, String requestId);
}
