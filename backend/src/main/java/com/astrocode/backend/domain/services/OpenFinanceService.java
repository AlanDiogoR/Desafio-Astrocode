package com.astrocode.backend.domain.services;

import com.astrocode.backend.domain.entities.BankAccount;
import com.astrocode.backend.domain.entities.PluggyItem;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.PlanUpgradeRequiredException;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import com.astrocode.backend.domain.model.enums.AccountType;
import com.astrocode.backend.domain.repositories.BankAccountRepository;
import com.astrocode.backend.domain.repositories.PluggyItemRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OpenFinanceService {

    private static final Logger log = LoggerFactory.getLogger(OpenFinanceService.class);

    private final PluggyApiClient pluggyApiClient;
    private final UserRepository userRepository;
    private final PluggyItemRepository pluggyItemRepository;
    private final BankAccountRepository bankAccountRepository;

    @Value("${app.frontend-url:}")
    private String frontendUrl;

    public OpenFinanceService(PluggyApiClient pluggyApiClient,
                              UserRepository userRepository,
                              PluggyItemRepository pluggyItemRepository,
                              BankAccountRepository bankAccountRepository) {
        this.pluggyApiClient = pluggyApiClient;
        this.userRepository = userRepository;
        this.pluggyItemRepository = pluggyItemRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    public String createConnectToken(UUID userId) {
        if (!pluggyApiClient.isConfigured()) {
            throw new IllegalStateException("Open Finance não está configurado. Entre em contato com o suporte.");
        }

        User user = userRepository.findByIdWithSubscription(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!user.isElite()) {
            throw new PlanUpgradeRequiredException(
                    "Open Finance está disponível no plano Elite Anual. Faça upgrade para conectar seus bancos.",
                    "openFinance");
        }

        String oauthRedirectUri = null;
        if (frontendUrl != null && !frontendUrl.isBlank()) {
            oauthRedirectUri = frontendUrl.trim().replaceAll("/$", "") + "/dashboard";
        }

        return pluggyApiClient.createConnectToken(userId, oauthRedirectUri);
    }

    @Transactional
    public List<BankAccount> syncAccountsFromPluggy(UUID userId, String pluggyItemId) {
        if (!pluggyApiClient.isConfigured()) {
            throw new IllegalStateException("Open Finance não está configurado.");
        }

        User user = userRepository.findByIdWithSubscription(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!user.isElite()) {
            throw new PlanUpgradeRequiredException(
                    "Open Finance está disponível no plano Elite Anual.",
                    "openFinance");
        }

        var itemDto = pluggyApiClient.fetchItem(pluggyItemId);
        var pluggyAccounts = pluggyApiClient.fetchAccounts(pluggyItemId);

        if (pluggyAccounts.isEmpty()) {
            return List.of();
        }

        var pluggyItem = pluggyItemRepository.findByPluggyItemId(pluggyItemId)
                .orElseGet(() -> {
                    var pi = PluggyItem.builder()
                            .user(user)
                            .pluggyItemId(pluggyItemId)
                            .institutionName(itemDto != null ? itemDto.institutionName() : null)
                            .status("UPDATED")
                            .build();
                    return pluggyItemRepository.save(pi);
                });

        var created = new java.util.ArrayList<BankAccount>();
        int updated = 0;
        for (var pa : pluggyAccounts) {
            var existingOpt = bankAccountRepository.findByPluggyAccountId(pa.id());
            if (existingOpt.isPresent()) {
                var existing = existingOpt.get();
                if (!existing.getUser().getId().equals(userId)) {
                    log.warn("Open Finance: pluggyAccountId {} pertence a outro usuário, ignorando", pa.id());
                    continue;
                }
                existing.setInitialBalance(pa.balance());
                existing.setCurrentBalance(pa.balance());
                bankAccountRepository.save(existing);
                updated++;
                continue;
            }

            String uniqueName = ensureUniqueName(user.getId(), pa.name(), pluggyItemId);

            var bankAccount = BankAccount.builder()
                    .user(user)
                    .name(uniqueName)
                    .initialBalance(pa.balance())
                    .currentBalance(pa.balance())
                    .type(AccountType.CHECKING)
                    .pluggyAccountId(pa.id())
                    .pluggyItemId(pluggyItemId)
                    .build();

            bankAccountRepository.save(bankAccount);
            created.add(bankAccount);
        }

        log.info("Open Finance: {} contas novas, {} contas atualizadas (saldo) para user {}", created.size(), updated, userId);
        return created;
    }

    private String ensureUniqueName(UUID userId, String baseName, String pluggyItemId) {
        String name = baseName != null && !baseName.isBlank() ? baseName : "Conta Open Finance";
        if (bankAccountRepository.findByUserIdAndNameIgnoreCase(userId, name).isEmpty()) {
            return name;
        }
        int suffix = 1;
        while (true) {
            String candidate = name + " (" + suffix + ")";
            if (bankAccountRepository.findByUserIdAndNameIgnoreCase(userId, candidate).isEmpty()) {
                return candidate;
            }
            suffix++;
        }
    }

    public boolean isConfigured() {
        return pluggyApiClient.isConfigured();
    }
}
