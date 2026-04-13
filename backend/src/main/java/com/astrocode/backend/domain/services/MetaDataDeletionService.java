package com.astrocode.backend.domain.services;

import com.astrocode.backend.domain.entities.DataDeletionRequest;
import com.astrocode.backend.domain.repositories.DataDeletionRequestRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class MetaDataDeletionService {

    private static final Logger log = LoggerFactory.getLogger(MetaDataDeletionService.class);

    private final UserRepository userRepository;
    private final DataDeletionRequestRepository deletionRequestRepository;

    public MetaDataDeletionService(UserRepository userRepository,
                                   DataDeletionRequestRepository deletionRequestRepository) {
        this.userRepository = userRepository;
        this.deletionRequestRepository = deletionRequestRepository;
    }

    @Transactional
    public String scheduleUserDataDeletion(String metaUserId) {
        String confirmationCode = UUID.randomUUID().toString();

        DataDeletionRequest request = DataDeletionRequest.builder()
                .confirmationCode(confirmationCode)
                .metaUserId(metaUserId)
                .status("pending")
                .requestedAt(OffsetDateTime.now())
                .scheduledFor(OffsetDateTime.now().plus(7, ChronoUnit.DAYS))
                .build();
        deletionRequestRepository.save(request);

        userRepository.findByMetaUserId(metaUserId)
                .ifPresent(user -> {
                    user.setScheduledForDeletion(true);
                    user.setDeletionScheduledAt(OffsetDateTime.now());
                    userRepository.save(user);
                    log.info("User {} scheduled for deletion", user.getId());
                });

        return confirmationCode;
    }

    public Optional<String> getDeletionStatus(String confirmationCode) {
        return deletionRequestRepository.findByConfirmationCode(confirmationCode)
                .map(DataDeletionRequest::getStatus);
    }
}
