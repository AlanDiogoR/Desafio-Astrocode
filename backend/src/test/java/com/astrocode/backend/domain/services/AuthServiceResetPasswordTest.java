package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.auth.ResetPasswordRequest;
import com.astrocode.backend.domain.entities.PasswordResetCode;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.InvalidResetCodeException;
import com.astrocode.backend.domain.repositories.PasswordResetCodeRepository;
import com.astrocode.backend.domain.repositories.SubscriptionRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Reset Password")
class AuthServiceResetPasswordTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PasswordResetCodeRepository resetCodeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private AuthService authService;

    private final String email = "test@example.com";
    private final UUID userId = UUID.randomUUID();

    private static String sha256(String input) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            var sb = new StringBuilder(64);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    private final User user = User.builder()
            .id(userId)
            .name("Test")
            .email(email)
            .password("oldEncoded")
            .build();

    @Test
    @DisplayName("resetPasswordWithCode_expiredCode_throws: código expirado lança InvalidResetCodeException")
    void resetPasswordWithCode_expiredCode_throws() {
        var expiredCode = PasswordResetCode.builder()
                .id(UUID.randomUUID())
                .email(email)
                .code(sha256("ABC123"))
                .expiresAt(OffsetDateTime.now().minusMinutes(1))
                .build();
        var request = new ResetPasswordRequest(email, "ABC123", "newPass123");

        when(resetCodeRepository.findFirstByEmailOrderByCreatedAtDesc(email))
                .thenReturn(Optional.of(expiredCode));

        assertThatThrownBy(() -> authService.resetPasswordWithCode(request))
                .isInstanceOf(InvalidResetCodeException.class);

        verify(resetCodeRepository).delete(expiredCode);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("resetPasswordWithCode_invalidCode_throws: código incorreto lança InvalidResetCodeException")
    void resetPasswordWithCode_invalidCode_throws() {
        var resetCode = PasswordResetCode.builder()
                .id(UUID.randomUUID())
                .email(email)
                .code(sha256("ABC123"))
                .expiresAt(OffsetDateTime.now().plusMinutes(10))
                .build();
        var request = new ResetPasswordRequest(email, "WRONG1", "newPass123");

        when(resetCodeRepository.findFirstByEmailOrderByCreatedAtDesc(email))
                .thenReturn(Optional.of(resetCode));

        assertThatThrownBy(() -> authService.resetPasswordWithCode(request))
                .isInstanceOf(InvalidResetCodeException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("resetPasswordWithCode_valid_updatesPassword: código válido atualiza senha e retorna token")
    void resetPasswordWithCode_valid_updatesPassword() {
        var resetCode = PasswordResetCode.builder()
                .id(UUID.randomUUID())
                .email(email)
                .code(sha256("ABC123"))
                .expiresAt(OffsetDateTime.now().plusMinutes(10))
                .build();
        var request = new ResetPasswordRequest(email, "ABC123", "newPass123");
        var encodedNewPassword = "encodedNewPass";

        when(resetCodeRepository.findFirstByEmailOrderByCreatedAtDesc(email))
                .thenReturn(Optional.of(resetCode));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.findByIdWithSubscription(userId)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPass123")).thenReturn(encodedNewPassword);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any(), anyString(), any(), any()))
                .thenReturn("jwt-token");

        var response = authService.resetPasswordWithCode(request);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.name()).isEqualTo("Test");
        verify(userRepository).save(argThat(u -> encodedNewPassword.equals(u.getPassword())));
        verify(resetCodeRepository).delete(resetCode);
    }
}
