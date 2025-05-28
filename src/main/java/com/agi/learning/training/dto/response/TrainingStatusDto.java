package com.agi.learning.training.dto.response;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingStatusDto {
    
    private String id;
    
    private String name;
    
    private String modelId;
    
    private String datasetId;
    
    private String status;
    
    private Integer currentEpoch;
    
    private Integer totalEpochs;
    
    private Double progress;
    
    private Map<String, Object> metrics;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    private Long trainingTimeSeconds;
    
    private String latestCheckpointId;
    
    private String errorMessage;
}
