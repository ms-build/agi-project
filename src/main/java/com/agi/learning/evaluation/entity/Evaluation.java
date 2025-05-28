package com.agi.learning.evaluation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ElementCollection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@Entity
@Table(name = "evaluation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluation {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String modelId;
    
    private String datasetId;
    
    @ElementCollection
    private Map<String, Double> metrics = new HashMap<>();
    
    private Double overallScore;
    
    @Column(nullable = false)
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    @Column(nullable = false)
    private String status;
    
    @Column(columnDefinition = "JSON")
    private String details;
}
