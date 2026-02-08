package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.user.UserRegistrationRequest;
import com.astrocode.backend.api.dto.user.UserResponse;
import com.astrocode.backend.domain.entities.Category;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.EmailAlreadyExistsException;
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

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt()
        );
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
