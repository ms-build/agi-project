package com.agi.ai.reasoning.repository;

import com.agi.ai.reasoning.entity.LogicalReasoning;
import com.agi.ai.reasoning.enums.ReasoningType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogicalReasoningRepository extends JpaRepository<LogicalReasoning, String> {
    
    List<LogicalReasoning> findByReasoningType(ReasoningType reasoningType);
    
    List<LogicalReasoning> findByIsValid(Boolean isValid);
    
    @Query("SELECT lr FROM LogicalReasoning lr WHERE lr.createdAt >= :startDate AND lr.createdAt <= :endDate")
    List<LogicalReasoning> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT lr FROM LogicalReasoning lr WHERE lr.confidenceScore >= :minScore")
    List<LogicalReasoning> findByConfidenceScoreGreaterThanEqual(Double minScore);
    
    @Query("SELECT lr FROM LogicalReasoning lr WHERE lr.text LIKE %:keyword% OR lr.conclusion LIKE %:keyword%")
    List<LogicalReasoning> findByTextOrConclusionContaining(String keyword);
}
