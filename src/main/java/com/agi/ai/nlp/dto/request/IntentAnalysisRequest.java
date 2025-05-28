package com.agi.ai.nlp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * 의도 분석 요청 DTO
 */
@Getter
@Builder
public class IntentAnalysisRequest {
    
    @NotBlank(message = "텍스트는 필수입니다")
    private String text;
    
    @NotNull(message = "대화 ID는 필수입니다")
    private String conversationId;
    
    private Long userId;
    
    private String context;
    
    private Boolean includeEntities;
    
    private Boolean includeSentiment;
}
