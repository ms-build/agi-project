package com.agi.ai.nlp.dto.response;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentAnalysisResultDto {
    
    private String text;
    
    private String sentiment;
    
    private Double positiveScore;
    
    private Double negativeScore;
    
    private Double neutralScore;
    
    private String language;
    
    private Boolean isSuccessful;
}
