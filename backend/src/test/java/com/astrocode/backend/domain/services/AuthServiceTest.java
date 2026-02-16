package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.auth.LoginRequest;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.InvalidCredentialsException;
import com.astrocode.backend.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthService")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User savedUser;

    @BeforeEach
    void setUp() {
        var user = User.builder()
                .name("Auth Test User")
                .email("auth.test@" + UUID.randomUUID() + ".com")
                .password(passwordEncoder.encode("senha123"))
                .build();
        savedUser = userRepository.save(user);
    }

    @Test
    @DisplayName("Deve retornar token e nome quando credenciais são válidas")
    void shouldReturnTokenAndNameWhenCredentialsValid() {
        var request = new LoginRequest(savedUser.getEmail(), "senha123");

        var response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isNotBlank();
        assertThat(response.name()).isEqualTo("Auth Test User");
    }

    @Test
    @DisplayName("Deve lançar InvalidCredentialsException quando email não existe")
    void shouldThrowWhenEmailNotFound() {
        var request = new LoginRequest("naoexiste@example.com", "senha123");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("Deve lançar InvalidCredentialsException quando senha está incorreta")
    void shouldThrowWhenPasswordWrong() {
        var request = new LoginRequest(savedUser.getEmail(), "senhaErrada");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
