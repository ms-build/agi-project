package com.agi.system.setting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * 설정 업데이트 요청 DTO
 */
@Getter
@Builder
public class SettingUpdateRequest {
    
    @NotBlank(message = "설정 키는 필수입니다")
    private String key;
    
    @NotNull(message = "설정 값은 필수입니다")
    private String value;
    
    private String description;
    
    private String category;
}
