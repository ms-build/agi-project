package com.agi.ai.generation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextGenerationRequest {
    
    @NotBlank(message = "프롬프트는 필수입니다")
    private String prompt;
    
    private Integer maxTokens;
    
    private Double temperature;
    
    private String[] stopSequences;
    
    private Boolean stream;
}
