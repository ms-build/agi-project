package com.agi.system.task.repository;

import com.agi.system.task.entity.Task;
import com.agi.system.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    
    List<Task> findByType(String type);
    
    List<Task> findByUserId(String userId);
    
    List<Task> findByStatus(TaskStatus status);
    
    List<Task> findByParentTaskId(String parentTaskId);
    
    @Query("SELECT t FROM Task t WHERE t.createdAt >= :startDate AND t.createdAt <= :endDate")
    List<Task> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Task t WHERE t.scheduledAt >= :startDate AND t.scheduledAt <= :endDate")
    List<Task> findByScheduledAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Task t WHERE t.name LIKE %:keyword% OR t.description LIKE %:keyword%")
    List<Task> findByNameOrDescriptionContaining(String keyword);
    
    List<Task> findByPriorityGreaterThanEqual(Integer minPriority);
}
