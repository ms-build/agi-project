package com.agi.learning.evaluation.repository;

import com.agi.learning.evaluation.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, String> {
    
    List<Evaluation> findByModelId(String modelId);
    
    List<Evaluation> findByDatasetId(String datasetId);
    
    List<Evaluation> findByStatus(String status);
    
    @Query("SELECT e FROM Evaluation e WHERE e.startedAt >= :startDate AND e.startedAt <= :endDate")
    List<Evaluation> findByStartedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT e FROM Evaluation e WHERE e.overallScore >= :minScore")
    List<Evaluation> findByOverallScoreGreaterThanEqual(Double minScore);
    
    @Query("SELECT e FROM Evaluation e WHERE e.name LIKE %:keyword%")
    List<Evaluation> findByNameContaining(String keyword);
}
