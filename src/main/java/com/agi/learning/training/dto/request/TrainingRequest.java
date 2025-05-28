package com.agi.learning.training.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingRequest {
    
    @NotBlank(message = "훈련 이름은 필수입니다")
    private String name;
    
    @NotNull(message = "모델 ID는 필수입니다")
    private String modelId;
    
    private String datasetId;
    
    private Integer epochs;
    
    private Integer batchSize;
    
    private Double learningRate;
    
    private Map<String, Object> hyperParameters;
    
    private Boolean saveCheckpoints;
    
    private Integer checkpointFrequency;
    
    private Boolean enableEarlyStopping;
}
