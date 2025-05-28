package com.agi.learning.evaluation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {
    
    @NotBlank(message = "평가 이름은 필수입니다")
    private String name;
    
    @NotNull(message = "모델 ID는 필수입니다")
    private String modelId;
    
    private String datasetId;
    
    private List<String> metrics;
    
    private Integer batchSize;
    
    private Boolean includeDetails;
}
