package com.astrocode.backend.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "webhook_processed_events", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"payment_id", "request_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "request_id", nullable = false, length = 255)
    private String requestId;

    @Column(name = "processed_at", nullable = false)
    private OffsetDateTime processedAt;

    public WebhookProcessedEvent(Long paymentId, String requestId) {
        this.paymentId = paymentId;
        this.requestId = requestId;
        this.processedAt = OffsetDateTime.now();
    }
}
