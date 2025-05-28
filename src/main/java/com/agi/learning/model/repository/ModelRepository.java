package com.agi.learning.model.repository;

import com.agi.learning.model.entity.Model;
import com.agi.learning.model.enums.ModelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, String> {
    
    List<Model> findByName(String name);
    
    List<Model> findByModelType(ModelType modelType);
    
    List<Model> findByStatus(String status);
    
    List<Model> findByDatasetId(String datasetId);
    
    @Query("SELECT m FROM Model m WHERE m.createdAt >= :startDate AND m.createdAt <= :endDate")
    List<Model> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT m FROM Model m WHERE m.name LIKE %:keyword%")
    List<Model> findByNameContaining(String keyword);
    
    @Query("SELECT m FROM Model m WHERE m.parametersCount >= :minCount")
    List<Model> findByParametersCountGreaterThanEqual(Long minCount);
}
