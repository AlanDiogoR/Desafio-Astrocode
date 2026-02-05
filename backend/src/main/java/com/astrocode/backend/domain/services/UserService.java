package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.UserRegistrationRequest;
import com.astrocode.backend.api.dto.UserResponse;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.EmailAlreadyExistsException;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável pela lógica de negócio relacionada a usuários.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra um novo usuário no sistema.
     * 
     * @param request DTO com os dados do usuário a ser cadastrado
     * @return UserResponse com os dados do usuário criado (sem senha)
     * @throws EmailAlreadyExistsException se o email já estiver cadastrado
     */
    public UserResponse register(UserRegistrationRequest request) {
        // Validação: verificar se email já existe
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        // Criar entidade User usando Builder do Lombok
        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        // Salvar no banco de dados
        var savedUser = userRepository.save(user);

        // Converter entidade para DTO de resposta (sem senha)
        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt()
        );
    }
}
