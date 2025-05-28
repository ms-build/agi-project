package com.agi.learning.model.dto.request;

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
public class ModelTrainingRequest {
    
    @NotBlank(message = "모델 이름은 필수입니다")
    private String name;
    
    @NotNull(message = "모델 타입은 필수입니다")
    private String modelType;
    
    private String datasetId;
    
    private Integer epochs;
    
    private Integer batchSize;
    
    private Double learningRate;
    
    private Map<String, Object> hyperParameters;
    
    private Boolean saveCheckpoints;
}
