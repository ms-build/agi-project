package com.agi.multimodal.video.dto.request;

import com.agi.multimodal.video.enums.VideoFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 비디오 업로드 요청 DTO
 */
@Getter
@Builder
public class VideoUploadRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotBlank(message = "비디오 데이터는 필수입니다")
    private String base64Data;
    
    private String filename;
    
    private VideoFormat format;
    
    private String description;
    
    private Integer duration;
    
    private Integer width;
    
    private Integer height;
    
    private List<String> tags;
}
