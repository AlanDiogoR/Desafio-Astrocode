package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.user.UpdateProfileRequest;
import com.astrocode.backend.api.dto.user.UserRegistrationRequest;
import com.astrocode.backend.api.dto.user.UserResponse;
import com.astrocode.backend.domain.entities.Category;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.EmailAlreadyExistsException;
import com.astrocode.backend.domain.exceptions.InvalidPasswordException;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.repositories.CategoryRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CategoryRepository categoryRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
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
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        var savedUser = userRepository.save(user);
        createDefaultCategories(savedUser);

        return toResponse(savedUser);
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
