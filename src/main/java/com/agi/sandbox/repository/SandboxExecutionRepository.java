package com.agi.sandbox.repository;

import com.agi.sandbox.entity.SandboxExecution;
import com.agi.sandbox.entity.Sandbox;
import com.agi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SandboxExecutionRepository extends JpaRepository<SandboxExecution, String> {
    List<SandboxExecution> findBySandbox(Sandbox sandbox);
    
    @Query("SELECT se FROM SandboxExecution se WHERE se.sandbox.id = :sandboxId")
    List<SandboxExecution> findBySandboxId(String sandboxId);
    
    @Query("SELECT se FROM SandboxExecution se WHERE se.sandbox.user.id = :userId")
    List<SandboxExecution> findByUserId(Long userId);
    
    @Query("SELECT se FROM SandboxExecution se WHERE se.startedAt >= :startDate AND se.startedAt <= :endDate")
    List<SandboxExecution> findByStartedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT se FROM SandboxExecution se WHERE se.exitCode != 0")
    List<SandboxExecution> findFailedExecutions();
}
