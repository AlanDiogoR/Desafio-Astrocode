package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA para a entidade User.
 * Fornece operações CRUD básicas e métodos customizados.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Busca um usuário pelo email.
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Verifica se existe um usuário com o email informado.
     * @param email Email a ser verificado
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);
}
