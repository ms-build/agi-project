package com.agi.multimodal.image.dto.request;

import com.agi.multimodal.image.enums.ImageFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 이미지 업로드 요청 DTO
 */
@Getter
@Builder
public class ImageUploadRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotBlank(message = "이미지 데이터는 필수입니다")
    private String base64Data;
    
    private String filename;
    
    private ImageFormat format;
    
    private String description;
    
    private List<String> tags;
}
