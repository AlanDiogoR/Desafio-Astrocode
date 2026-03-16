package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.account.BankAccountRequest;
import com.astrocode.backend.domain.entities.BankAccount;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.DuplicateAccountNameException;
import com.astrocode.backend.domain.exceptions.ResourceAccessDeniedException;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import com.astrocode.backend.domain.model.enums.AccountType;
import com.astrocode.backend.domain.repositories.BankAccountRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BankAccountService - Unidade")
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    private final UUID userId = UUID.randomUUID();
    private final User user = User.builder().id(userId).name("Test").email("test@test.com").password("x").build();

    @Test
    @DisplayName("createAccount_success: dados válidos, salva e retorna a conta criada")
    void createAccount_success() {
        var request = new BankAccountRequest("Conta Corrente", BigDecimal.valueOf(1000), AccountType.CHECKING, "#087f5b");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bankAccountRepository.findByUserIdAndNameIgnoreCase(userId, "Conta Corrente")).thenReturn(List.of());
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(inv -> {
            var acc = inv.getArgument(0, BankAccount.class);
            acc.setId(UUID.randomUUID());
            return acc;
        });

        var result = bankAccountService.create(request, userId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Conta Corrente");
        assertThat(result.getCurrentBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        verify(bankAccountRepository).save(any(BankAccount.class));
    }

    @Test
    @DisplayName("createAccount_duplicateName: já existe conta com o mesmo nome, lança DuplicateAccountNameException")
    void createAccount_duplicateName() {
        var request = new BankAccountRequest("Conta Corrente", BigDecimal.valueOf(1000), AccountType.CHECKING, null);
        var existing = BankAccount.builder().id(UUID.randomUUID()).name("Conta Corrente").user(user).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bankAccountRepository.findByUserIdAndNameIgnoreCase(userId, "Conta Corrente")).thenReturn(List.of(existing));

        assertThatThrownBy(() -> bankAccountService.create(request, userId))
                .isInstanceOf(DuplicateAccountNameException.class)
                .hasMessageContaining("Conta Corrente");

        verify(bankAccountRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteAccount_notOwner: conta pertence a outro usuário, lança exceção de autorização")
    void deleteAccount_notOwner() {
        var accountId = UUID.randomUUID();
        var otherUser = User.builder().id(UUID.randomUUID()).name("Other").email("other@test.com").password("x").build();
        var account = BankAccount.builder().id(accountId).user(otherUser).build();

        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> bankAccountService.delete(accountId, user))
                .isInstanceOf(ResourceAccessDeniedException.class);

        verify(bankAccountRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteAccount_success: conta pertence ao usuário, deleta")
    void deleteAccount_success() {
        var accountId = UUID.randomUUID();
        var account = BankAccount.builder().id(accountId).user(user).build();

        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        doNothing().when(bankAccountRepository).delete(account);

        bankAccountService.delete(accountId, user);

        verify(bankAccountRepository).delete(account);
    }
}
