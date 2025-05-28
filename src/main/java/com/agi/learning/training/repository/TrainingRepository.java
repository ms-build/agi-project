package com.agi.learning.training.repository;

import com.agi.learning.training.entity.Training;
import com.agi.learning.training.enums.TrainingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, String> {
    
    List<Training> findByModelId(String modelId);
    
    List<Training> findByDatasetId(String datasetId);
    
    List<Training> findByStatus(TrainingStatus status);
    
    @Query("SELECT t FROM Training t WHERE t.startedAt >= :startDate AND t.startedAt <= :endDate")
    List<Training> findByStartedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Training t WHERE t.name LIKE %:keyword%")
    List<Training> findByNameContaining(String keyword);
    
    @Query("SELECT t FROM Training t WHERE t.progress >= :minProgress")
    List<Training> findByProgressGreaterThanEqual(Double minProgress);
    
    List<Training> findByEnableEarlyStopping(Boolean enableEarlyStopping);
}
