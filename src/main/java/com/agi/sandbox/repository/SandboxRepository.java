package com.agi.sandbox.repository;

import com.agi.sandbox.entity.Sandbox;
import com.agi.sandbox.enums.SandboxStatus;
import com.agi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SandboxRepository extends JpaRepository<Sandbox, String> {
    List<Sandbox> findByUser(User user);
    
    List<Sandbox> findByStatus(SandboxStatus status);
    
    @Query("SELECT s FROM Sandbox s WHERE s.user.id = :userId")
    List<Sandbox> findByUserId(Long userId);
    
    @Query("SELECT s FROM Sandbox s WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate")
    List<Sandbox> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT s FROM Sandbox s WHERE s.lastActiveAt < :date")
    List<Sandbox> findInactiveSandboxes(LocalDateTime date);
    
    @Query("SELECT s FROM Sandbox s WHERE s.status = :status AND s.user.id = :userId")
    List<Sandbox> findByStatusAndUserId(SandboxStatus status, Long userId);
}
