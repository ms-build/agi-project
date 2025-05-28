package com.agi.ai.reasoning.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogicalReasoningRequest {
    
    @NotBlank(message = "추론 대상 텍스트는 필수입니다")
    private String text;
    
    private String context;
    
    private Boolean includeExplanation;
    
    private String reasoningType;
}
