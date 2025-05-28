package com.agi.learning.training.entity;

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

import com.agi.learning.training.enums.TrainingStatus;

@Entity
@Table(name = "training")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Training {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String modelId;
    
    private String datasetId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainingStatus status;
    
    private Integer currentEpoch;
    
    private Integer totalEpochs;
    
    private Double progress;
    
    @ElementCollection
    private Map<String, Object> metrics = new HashMap<>();
    
    @Column(nullable = false)
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    private Long trainingTimeSeconds;
    
    private String latestCheckpointId;
    
    private String errorMessage;
    
    private Boolean saveCheckpoints;
    
    private Integer checkpointFrequency;
    
    private Boolean enableEarlyStopping;
}
