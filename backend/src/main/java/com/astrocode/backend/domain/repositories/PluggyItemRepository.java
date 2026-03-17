package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.PluggyItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PluggyItemRepository extends JpaRepository<PluggyItem, UUID> {

    Optional<PluggyItem> findByPluggyItemId(String pluggyItemId);

    @Query("SELECT p FROM PluggyItem p WHERE p.user.id = :userId")
    List<PluggyItem> findByUserId(@Param("userId") UUID userId);

    boolean existsByPluggyItemId(String pluggyItemId);
}
