package com.agi.ai.reasoning.dto.response;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogicalReasoningResultDto {
    
    private String text;
    
    private String conclusion;
    
    private List<String> reasoningSteps;
    
    private String explanation;
    
    private Double confidenceScore;
    
    private Boolean isValid;
    
    private Boolean isSuccessful;
}
