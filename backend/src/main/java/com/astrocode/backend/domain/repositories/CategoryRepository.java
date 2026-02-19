package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.Category;
import com.astrocode.backend.domain.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Query("SELECT c FROM Category c WHERE c.user.id = :userId")
    List<Category> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND c.type = :type")
    List<Category> findByUserIdAndType(@Param("userId") UUID userId, @Param("type") TransactionType type);
}
