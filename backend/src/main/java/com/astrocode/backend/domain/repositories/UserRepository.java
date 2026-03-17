package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.subscription WHERE u.id = :userId")
    Optional<User> findByIdWithSubscription(@Param("userId") UUID userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.subscription WHERE u.email = :email")
    Optional<User> findByEmailWithSubscription(@Param("email") String email);

    boolean existsByEmail(String email);
}
