package com.agi.system.setting.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingUpdateRequest {
    
    @NotBlank(message = "설정 키는 필수입니다")
    private String key;
    
    private String value;
    
    private String description;
    
    private boolean isGlobal;
    
    private Long userId;
}
