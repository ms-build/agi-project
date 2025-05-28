package com.agi.ai.vision.dto.response;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAnalysisResultDto {
    
    private String imageUrl;
    
    private List<String> labels;
    
    private List<Map<String, Object>> detectedObjects;
    
    private List<Map<String, Object>> detectedFaces;
    
    private Map<String, Double> confidenceScores;
    
    private String dominantColor;
    
    private Boolean isSuccessful;
    
    private String errorMessage;
}
