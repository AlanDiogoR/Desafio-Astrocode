package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.auth.LoginRequest;
import com.astrocode.backend.api.dto.auth.LoginResponse;
import com.astrocode.backend.api.dto.auth.ResetPasswordRequest;
import com.astrocode.backend.domain.entities.PasswordResetCode;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.InvalidCredentialsException;
import com.astrocode.backend.domain.exceptions.InvalidResetCodeException;
import com.astrocode.backend.domain.repositories.PasswordResetCodeRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRATION_MINUTES = 15;
    private static final int RATE_LIMIT_MINUTES = 2;

    private static final String ALPHANUMERIC = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository resetCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;

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

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return new LoginResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    @Transactional(readOnly = false)
    public void requestPasswordReset(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        OffsetDateTime rateLimitThreshold = OffsetDateTime.now().minusMinutes(RATE_LIMIT_MINUTES);

        if (resetCodeRepository.existsByEmailAndCreatedAtAfter(normalizedEmail, rateLimitThreshold)) {
            return;
        }

        if (!userRepository.existsByEmail(normalizedEmail)) {
            return;
        }

        resetCodeRepository.deleteByEmail(normalizedEmail);

        String code = generateAlphanumericCode();
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);

        var resetCode = PasswordResetCode.builder()
                .email(normalizedEmail)
                .code(code)
                .expiresAt(expiresAt)
                .build();
        resetCodeRepository.save(resetCode);

        mailService.sendPasswordResetCode(normalizedEmail, code);
    }

    @Transactional(readOnly = false)
    public LoginResponse resetPasswordWithCode(ResetPasswordRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        PasswordResetCode resetCode = resetCodeRepository.findFirstByEmailOrderByCreatedAtDesc(normalizedEmail)
                .orElseThrow(InvalidResetCodeException::new);

        if (!resetCode.getCode().equals(request.code())) {
            throw new InvalidResetCodeException();
        }
        if (OffsetDateTime.now().isAfter(resetCode.getExpiresAt())) {
            resetCodeRepository.delete(resetCode);
            throw new InvalidResetCodeException();
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(InvalidResetCodeException::new);

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        resetCodeRepository.delete(resetCode);

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return new LoginResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    private String generateAlphanumericCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
}
