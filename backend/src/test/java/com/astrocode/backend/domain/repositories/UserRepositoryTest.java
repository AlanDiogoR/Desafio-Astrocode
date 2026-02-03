package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de integração para o repositório UserRepository.
 * Testa operações CRUD básicas e métodos customizados.
 * 
 * @SpringBootTest carrega o contexto completo da aplicação.
 * @ActiveProfiles("test") usa configurações de teste (PostgreSQL real).
 * @Transactional garante que cada teste é executado em uma transação isolada e faz rollback ao final.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes do Repositório de Usuários")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve salvar um novo usuário com sucesso")
    void shouldSaveUser() {
        // Arrange - Preparar dados
        User user = User.builder()
                .name("João Silva")
                .email("joao.silva@example.com")
                .password("senha123")
                .build();

        // Act - Executar ação
        User savedUser = userRepository.save(user);

        // Assert - Verificar resultados
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("João Silva");
        assertThat(savedUser.getEmail()).isEqualTo("joao.silva@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("senha123");
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isBefore(OffsetDateTime.now().plusSeconds(1));
    }

    @Test
    @DisplayName("Deve buscar um usuário pelo ID")
    void shouldFindUserById() {
        // Arrange
        User user = User.builder()
                .name("Maria Santos")
                .email("maria.santos@example.com")
                .password("senha456")
                .build();
        User savedUser = userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.get().getName()).isEqualTo("Maria Santos");
        assertThat(foundUser.get().getEmail()).isEqualTo("maria.santos@example.com");
    }

    @Test
    @DisplayName("Deve buscar um usuário pelo email")
    void shouldFindUserByEmail() {
        // Arrange
        User user = User.builder()
                .name("Pedro Costa")
                .email("pedro.costa@example.com")
                .password("senha789")
                .build();
        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("pedro.costa@example.com");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("pedro.costa@example.com");
        assertThat(foundUser.get().getName()).isEqualTo("Pedro Costa");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando email não existe")
    void shouldReturnEmptyWhenEmailNotFound() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("nao.existe@example.com");

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Deve verificar se email existe")
    void shouldCheckIfEmailExists() {
        // Arrange
        User user = User.builder()
                .name("Ana Oliveira")
                .email("ana.oliveira@example.com")
                .password("senha101112")
                .build();
        userRepository.save(user);

        // Act & Assert
        assertThat(userRepository.existsByEmail("ana.oliveira@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nao.existe@example.com")).isFalse();
    }

    @Test
    @DisplayName("Deve atualizar um usuário existente")
    void shouldUpdateUser() throws InterruptedException {
        // Arrange
        User user = User.builder()
                .name("Carlos Mendes")
                .email("carlos.mendes@example.com")
                .password("senha131415")
                .build();
        User savedUser = userRepository.save(user);
        OffsetDateTime originalUpdatedAt = savedUser.getUpdatedAt();

        // Act
        savedUser.setName("Carlos Mendes Silva");
        Thread.sleep(10); // Pequeno delay para garantir que updatedAt seja diferente
        User updatedUser = userRepository.save(savedUser);

        // Assert
        assertThat(updatedUser.getName()).isEqualTo("Carlos Mendes Silva");
        assertThat(updatedUser.getUpdatedAt()).isAfter(originalUpdatedAt);
        assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("Deve deletar um usuário")
    void shouldDeleteUser() {
        // Arrange
        User user = User.builder()
                .name("Fernanda Lima")
                .email("fernanda.lima@example.com")
                .password("senha161718")
                .build();
        User savedUser = userRepository.save(user);
        UUID userId = savedUser.getId();

        // Act
        userRepository.delete(savedUser);

        // Assert
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void shouldFindAllUsers() {
        // Arrange
        User user1 = User.builder()
                .name("Usuário 1")
                .email("usuario1@example.com")
                .password("senha1")
                .build();
        User user2 = User.builder()
                .name("Usuário 2")
                .email("usuario2@example.com")
                .password("senha2")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);

        // Act
        long count = userRepository.count();

        // Assert
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Deve criar timestamps automaticamente ao salvar")
    void shouldCreateTimestampsAutomatically() {
        // Arrange
        User user = User.builder()
                .name("Teste Timestamps")
                .email("teste.timestamps@example.com")
                .password("senha192021")
                .build();

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isEqualTo(savedUser.getUpdatedAt());
    }
}
