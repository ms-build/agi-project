package com.agi.ai.nlp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentAnalysisRequest {
    
    @NotBlank(message = "텍스트 내용은 필수입니다")
    private String text;
    
    private String language;
    
    private Boolean includeDetails;
}
