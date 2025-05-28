package com.agi.ai.generation.dto.response;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextGenerationResultDto {
    
    private String prompt;
    
    private String generatedText;
    
    private Integer tokensUsed;
    
    private Double completionTime;
    
    private Boolean isSuccessful;
    
    private String errorMessage;
}
