package com.astrocode.backend.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "whatsapp_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsappMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "raw_message", nullable = false, columnDefinition = "text")
    private String rawMessage;

    @Column(name = "extracted_data", columnDefinition = "text")
    private String extractedDataJson;

    @Column(name = "processed_at", nullable = false)
    private OffsetDateTime processedAt;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "pending";

    @PrePersist
    void prePersist() {
        if (processedAt == null) {
            processedAt = OffsetDateTime.now();
        }
    }
}
