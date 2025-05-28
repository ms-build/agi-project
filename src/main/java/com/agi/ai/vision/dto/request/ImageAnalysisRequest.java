package com.agi.ai.vision.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAnalysisRequest {
    
    @NotBlank(message = "이미지 URL은 필수입니다")
    private String imageUrl;
    
    private String[] features;
    
    private Boolean includeLabels;
    
    private Boolean includeObjects;
    
    private Boolean includeFaces;
}
