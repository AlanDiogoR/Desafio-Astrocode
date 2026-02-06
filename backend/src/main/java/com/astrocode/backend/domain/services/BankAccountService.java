package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.BankAccountRequest;
import com.astrocode.backend.domain.entities.BankAccount;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.ResourceAccessDeniedException;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import com.astrocode.backend.domain.repositories.BankAccountRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BankAccount create(BankAccountRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        var bankAccount = BankAccount.builder()
                .user(user)
                .name(request.name())
                .initialBalance(request.initialBalance())
                .type(request.type())
                .color(request.color())
                .build();

        return bankAccountRepository.save(bankAccount);
    }

    public List<BankAccount> findAllByUserId(UUID userId) {
        return bankAccountRepository.findByUserId(userId);
    }

    @Transactional
    public BankAccount update(UUID accountId, BankAccountRequest request, User user) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("Você não tem permissão para acessar esta conta");
        }

        account.setName(request.name());
        account.setInitialBalance(request.initialBalance());
        account.setType(request.type());
        account.setColor(request.color());

        return bankAccountRepository.save(account);
    }

    @Transactional
    public void delete(UUID accountId, User user) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new ResourceAccessDeniedException("Você não tem permissão para acessar esta conta");
        }

        bankAccountRepository.delete(account);
    }
}
