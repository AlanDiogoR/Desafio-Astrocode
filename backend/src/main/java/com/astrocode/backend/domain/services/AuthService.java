package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.auth.LoginRequest;
import com.astrocode.backend.api.dto.auth.LoginResponse;
import com.astrocode.backend.api.dto.auth.ResetPasswordRequest;
import com.astrocode.backend.domain.entities.PasswordResetCode;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.EmailNotVerifiedException;
import com.astrocode.backend.domain.exceptions.InvalidCredentialsException;
import com.astrocode.backend.domain.exceptions.InvalidResetCodeException;
import com.astrocode.backend.domain.exceptions.InvalidTokenException;
import com.astrocode.backend.domain.repositories.PasswordResetCodeRepository;
import com.astrocode.backend.domain.model.enums.PlanType;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRATION_MINUTES = 15;
    private static final int RATE_LIMIT_MINUTES = 2;
    private static final long ACCESS_EXPIRES_SECONDS = 900L;

    private static final String ALPHANUMERIC = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository resetCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;

    @Value("${app.security.password-reset-min-duration-ms:200}")
    private long passwordResetMinDurationMs;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    public AuthService(UserRepository userRepository,
                       PasswordResetCodeRepository resetCodeRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       MailService mailService) {
        this.userRepository = userRepository;
        this.resetCodeRepository = resetCodeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailService = mailService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailWithSubscription(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException();
        }

        return buildLoginResponse(user);
    }

    @Transactional
    public LoginResponse refresh(String refreshTokenRaw) {
        if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) {
            throw new InvalidTokenException("Refresh token ausente");
        }
        String hash = hashWithSHA256(refreshTokenRaw);
        User user = userRepository.findByRefreshTokenHash(hash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token inválido ou expirado"));

        user = userRepository.findByIdWithSubscription(user.getId()).orElse(user);

        user.setRefreshTokenHash(null);
        userRepository.saveAndFlush(user);

        return buildLoginResponse(user);
    }

    private LoginResponse buildLoginResponse(User user) {
        var sub = user.getSubscription();
        var planType = sub != null ? sub.getPlanType() : PlanType.FREE;

        String accessToken = jwtService.generateToken(user.getId(), user.getEmail(),
                planType, sub != null ? sub.getExpiresAt() : null);

        String refreshPlain = newOpaqueRefreshToken();
        user.setRefreshTokenHash(hashWithSHA256(refreshPlain));
        userRepository.save(user);

        return new LoginResponse(
                accessToken,
                refreshPlain,
                ACCESS_EXPIRES_SECONDS,
                user.getId(),
                user.getName(),
                user.getEmail(),
                planType.name(),
                user.isPro(),
                user.isElite(),
                sub != null ? sub.getExpiresAt() : null
        );
    }

    private String newOpaqueRefreshToken() {
        byte[] bytes = new byte[48];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Transactional(readOnly = false)
    public void requestPasswordReset(String email) {
        long startNanos = System.nanoTime();
        try {
            String normalizedEmail = email.trim().toLowerCase();
            OffsetDateTime rateLimitThreshold = OffsetDateTime.now().minusMinutes(RATE_LIMIT_MINUTES);

            boolean rateLimited = resetCodeRepository.existsByEmailAndCreatedAtAfter(normalizedEmail, rateLimitThreshold);
            boolean userExists = userRepository.existsByEmail(normalizedEmail);

            hashWithSHA256(normalizedEmail + "|reset|" + userExists + "|" + rateLimited);

            if (!userExists) {
                return;
            }

            if (rateLimited) {
                return;
            }

            resetCodeRepository.deleteByEmail(normalizedEmail);

            String code = generateAlphanumericCode();
            String hashedCode = hashWithSHA256(code);
            OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);

            var resetCode = PasswordResetCode.builder()
                    .email(normalizedEmail)
                    .code(hashedCode)
                    .expiresAt(expiresAt)
                    .build();
            resetCodeRepository.save(resetCode);

            mailService.sendPasswordResetCode(normalizedEmail, code);
        } finally {
            enforceMinimumResetDuration(startNanos);
        }
    }

    @Transactional(readOnly = false)
    public void resendVerificationEmail(String email) {
        String normalized = email.trim().toLowerCase();
        Optional<User> opt = userRepository.findByEmailWithSubscription(normalized);
        if (opt.isEmpty()) {
            return;
        }
        User user = opt.get();
        if (user.isEmailVerified()) {
            return;
        }
        String token = java.util.UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        userRepository.save(user);
        String base = frontendUrl != null ? frontendUrl.replaceAll("/$", "") : "http://localhost:3000";
        mailService.sendEmailVerification(normalized, base + "/verify-email?token=" + token);
    }

    private void enforceMinimumResetDuration(long startNanos) {
        long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
        long sleepMs = passwordResetMinDurationMs - elapsedMs;
        if (sleepMs > 0) {
            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Transactional(readOnly = false)
    public LoginResponse resetPasswordWithCode(ResetPasswordRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        PasswordResetCode resetCode = resetCodeRepository.findFirstByEmailOrderByCreatedAtDesc(normalizedEmail)
                .orElseThrow(InvalidResetCodeException::new);

        String hashedRequestCode = hashWithSHA256(request.code());
        if (!MessageDigest.isEqual(
                resetCode.getCode().getBytes(StandardCharsets.UTF_8),
                hashedRequestCode.getBytes(StandardCharsets.UTF_8))) {
            throw new InvalidResetCodeException();
        }
        if (OffsetDateTime.now().isAfter(resetCode.getExpiresAt())) {
            resetCodeRepository.delete(resetCode);
            throw new InvalidResetCodeException();
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(InvalidResetCodeException::new);
        user = userRepository.findByIdWithSubscription(user.getId()).orElse(user);

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
        resetCodeRepository.delete(resetCode);

        return buildLoginResponse(user);
    }

    private String generateAlphanumericCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    private String hashWithSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(64);
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 não disponível", e);
        }
    }
}
