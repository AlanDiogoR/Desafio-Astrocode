package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.SavingsGoalRequest;
import com.astrocode.backend.domain.entities.SavingsGoal;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.ResourceAccessDeniedException;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import com.astrocode.backend.domain.model.enums.GoalStatus;
import com.astrocode.backend.domain.repositories.SavingsGoalRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;

    public SavingsGoalService(SavingsGoalRepository savingsGoalRepository, UserRepository userRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SavingsGoal create(SavingsGoalRequest request, UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (request.targetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor alvo deve ser maior que zero");
        }

        var savingsGoal = SavingsGoal.builder()
                .user(user)
                .name(request.name())
                .targetAmount(request.targetAmount())
                .currentAmount(BigDecimal.ZERO)
                .startDate(LocalDate.now())
                .status(GoalStatus.ACTIVE)
                .color(request.color())
                .build();

        return savingsGoalRepository.save(savingsGoal);
    }

    public List<SavingsGoal> findAllByUserId(UUID userId) {
        return savingsGoalRepository.findByUserId(userId);
    }

    public SavingsGoal findByIdAndUserId(UUID id, UUID userId) {
        var savingsGoal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta de poupança não encontrada"));

        if (!savingsGoal.getUser().getId().equals(userId)) {
            throw new ResourceAccessDeniedException("Você não tem permissão para acessar esta meta");
        }

        return savingsGoal;
    }

    @Transactional
    public SavingsGoal update(UUID id, SavingsGoalRequest request, User user) {
        var savingsGoal = findByIdAndUserId(id, user.getId());

        if (request.targetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor alvo deve ser maior que zero");
        }

        savingsGoal.setName(request.name());
        savingsGoal.setTargetAmount(request.targetAmount());
        savingsGoal.setColor(request.color());

        return savingsGoalRepository.save(savingsGoal);
    }

    @Transactional
    public SavingsGoal updateAmount(UUID id, BigDecimal amount, User user) {
        var savingsGoal = findByIdAndUserId(id, user.getId());

        BigDecimal newAmount = savingsGoal.getCurrentAmount().add(amount);
        savingsGoal.setCurrentAmount(newAmount);

        return savingsGoalRepository.save(savingsGoal);
    }

    @Transactional
    public void delete(UUID id, User user) {
        var savingsGoal = findByIdAndUserId(id, user.getId());
        savingsGoalRepository.delete(savingsGoal);
    }
}
