package com.agi.multimodal.audio.dto.request;

import com.agi.multimodal.audio.enums.AudioFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 오디오 업로드 요청 DTO
 */
@Getter
@Builder
public class AudioUploadRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotBlank(message = "오디오 데이터는 필수입니다")
    private String base64Data;
    
    private String filename;
    
    private AudioFormat format;
    
    private String description;
    
    private Integer duration;
    
    private List<String> tags;
}
