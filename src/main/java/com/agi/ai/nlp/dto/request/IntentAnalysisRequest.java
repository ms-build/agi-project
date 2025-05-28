package com.agi.ai.nlp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntentAnalysisRequest {
    
    @NotBlank(message = "텍스트는 필수입니다")
    private String text;
    
    private String conversationId;
    
    private Long userId;
    
    private Map<String, Object> context;
    
    private Double confidenceThreshold;
}
