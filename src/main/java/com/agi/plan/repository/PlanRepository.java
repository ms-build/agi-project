package com.agi.plan.repository;

import com.agi.plan.entity.Plan;
import com.agi.plan.enums.PlanStatus;
import com.agi.conversation.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, String> {
    List<Plan> findByConversation(Conversation conversation);
    
    List<Plan> findByStatus(PlanStatus status);
    
    @Query("SELECT p FROM Plan p WHERE p.conversation.id = :conversationId")
    List<Plan> findByConversationId(String conversationId);
    
    @Query("SELECT p FROM Plan p WHERE p.conversation.user.id = :userId")
    List<Plan> findByUserId(Long userId);
    
    @Query("SELECT p FROM Plan p WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate")
    List<Plan> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT p FROM Plan p WHERE p.status = :status AND p.conversation.user.id = :userId")
    List<Plan> findByStatusAndUserId(PlanStatus status, Long userId);
}
