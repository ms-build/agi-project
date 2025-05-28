package com.agi.learning.model.dto.response;

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
public class ModelInfoDto {
    
    private String id;
    
    private String name;
    
    private String modelType;
    
    private String version;
    
    private String status;
    
    private Map<String, Object> metrics;
    
    private Map<String, Object> hyperParameters;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime lastTrainedAt;
    
    private Long trainingTimeSeconds;
    
    private String datasetId;
    
    private Long parametersCount;
}
