package com.agi.learning.evaluation.dto.response;

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
public class EvaluationResultDto {
    
    private String id;
    
    private String name;
    
    private String modelId;
    
    private String datasetId;
    
    private Map<String, Double> metrics;
    
    private Double overallScore;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    private String status;
    
    private Map<String, Object> details;
}
