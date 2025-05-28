package com.agi.system.setting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingDto {
    private String id;
    private String key;
    private String value;
    private String description;
    private boolean isGlobal;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
