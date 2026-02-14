package com.astrocode.backend.domain.repositories;

import com.astrocode.backend.domain.entities.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, UUID> {
    
    @Query("SELECT sg FROM SavingsGoal sg WHERE sg.user.id = :userId AND sg.deletedAt IS NULL")
    List<SavingsGoal> findByUserId(@Param("userId") UUID userId);
}
