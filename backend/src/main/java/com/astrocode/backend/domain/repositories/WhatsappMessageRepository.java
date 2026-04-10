package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.WhatsappMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WhatsappMessageRepository extends JpaRepository<WhatsappMessage, UUID> {
}
