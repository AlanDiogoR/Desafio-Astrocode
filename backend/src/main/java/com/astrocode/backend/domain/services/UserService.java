package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.user.UpdateProfileRequest;
import com.astrocode.backend.api.dto.user.UserRegistrationRequest;
import com.astrocode.backend.api.dto.user.UserResponse;
import com.astrocode.backend.domain.entities.Category;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.EmailAlreadyExistsException;
import com.astrocode.backend.domain.exceptions.InvalidPasswordException;
import com.astrocode.backend.domain.exceptions.InvalidTokenException;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.repositories.CategoryRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import com.astrocode.backend.domain.repositories.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Value("${app.frontend-url:https://grivy.netlify.app}")
    private String frontendUrl;

    public UserService(UserRepository userRepository, CategoryRepository categoryRepository,
                       SubscriptionRepository subscriptionRepository, PasswordEncoder passwordEncoder,
                       MailService mailService) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    public UserResponse updateProfile(User user, UpdateProfileRequest request) {
        boolean updatingName = request.name() != null && !request.name().isBlank();
        boolean updatingPassword = request.newPassword() != null && !request.newPassword().isBlank();

        if (updatingPassword) {
            if (request.currentPassword() == null || request.currentPassword().isBlank()) {
                throw new InvalidPasswordException("Informe a senha atual para alterar");
            }
            if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
                throw new InvalidPasswordException();
            }
        }

        if (updatingName) {
            user.setName(request.name().trim());
        }
        if (updatingPassword) {
            user.setPassword(passwordEncoder.encode(request.newPassword()));
        }

        return toResponse(userRepository.save(user));
    }

    public UserResponse toResponse(User user) {
        var sub = user.getSubscription();
        var planType = sub != null ? sub.getPlanType() : com.astrocode.backend.domain.model.enums.PlanType.FREE;
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                planType.name(),
                user.isPro(),
                user.isElite(),
                sub != null ? sub.getExpiresAt() : null,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        String verificationToken = UUID.randomUUID().toString();
        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .emailVerified(false)
                .emailVerificationToken(verificationToken)
                .build();

        User savedUser;
        try {
            savedUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException(request.email(), e);
        }
        createDefaultCategories(savedUser);

        subscriptionRepository.save(
                com.astrocode.backend.domain.entities.Subscription.builder()
                        .user(savedUser)
                        .planType(com.astrocode.backend.domain.model.enums.PlanType.FREE)
                        .status(com.astrocode.backend.domain.model.enums.SubscriptionStatus.ACTIVE)
                        .build()
        );

        String base = frontendUrl != null && !frontendUrl.isBlank()
                ? frontendUrl.replaceAll("/$", "")
                : "https://grivy.netlify.app";
        try {
            mailService.sendEmailVerification(savedUser.getEmail(), base + "/verify-email?token=" + verificationToken);
        } catch (Exception e) {
            log.warn("[MAIL] Falha ao enviar e-mail de verificação para {}: {}", savedUser.getEmail(), e.getMessage());
        }

        return toResponse(userRepository.findByIdWithSubscription(savedUser.getId()).orElse(savedUser));
    }

    @Transactional
    public void verifyEmail(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException("Token inválido");
        }
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token inválido ou expirado"));
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
    }

    @Transactional
    public void deleteCurrentUser(User principal, String password) {
        if (!passwordEncoder.matches(password, principal.getPassword())) {
            throw new InvalidPasswordException();
        }
        userRepository.delete(principal);
    }

    private void createDefaultCategories(User user) {
        List.of(
                new CategoryData("Salário", "salary", TransactionType.INCOME),
                new CategoryData("Freelance", "freelance", TransactionType.INCOME),
                new CategoryData("Outro", "other", TransactionType.INCOME),
                new CategoryData("Casa", "home", TransactionType.EXPENSE),
                new CategoryData("Alimentação", "food", TransactionType.EXPENSE),
                new CategoryData("Educação", "education", TransactionType.EXPENSE),
                new CategoryData("Lazer", "fun", TransactionType.EXPENSE),
                new CategoryData("Mercado", "grocery", TransactionType.EXPENSE),
                new CategoryData("Roupas", "clothes", TransactionType.EXPENSE),
                new CategoryData("Transporte", "transport", TransactionType.EXPENSE),
                new CategoryData("Viagem", "travel", TransactionType.EXPENSE),
                new CategoryData("Outro", "other", TransactionType.EXPENSE)
        ).forEach(data -> {
            var category = Category.builder()
                    .user(user)
                    .name(data.name())
                    .icon(data.icon())
                    .type(data.type())
                    .build();
            categoryRepository.save(category);
        });
    }

    private record CategoryData(String name, String icon, TransactionType type) {}
}
