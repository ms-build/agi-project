package com.agi.tool.repository;

import com.agi.tool.entity.ToolExecution;
import com.agi.tool.entity.Tool;
import com.agi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ToolExecutionRepository extends JpaRepository<ToolExecution, String> {
    List<ToolExecution> findByTool(Tool tool);
    
    List<ToolExecution> findByUser(User user);
    
    @Query("SELECT te FROM ToolExecution te WHERE te.tool.id = :toolId")
    List<ToolExecution> findByToolId(String toolId);
    
    @Query("SELECT te FROM ToolExecution te WHERE te.user.id = :userId")
    List<ToolExecution> findByUserId(Long userId);
    
    @Query("SELECT te FROM ToolExecution te WHERE te.executedAt >= :startDate AND te.executedAt <= :endDate")
    List<ToolExecution> findByExecutedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
