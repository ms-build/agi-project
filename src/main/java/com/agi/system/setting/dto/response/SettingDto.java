package com.agi.system.setting.dto.response;

import com.agi.system.setting.entity.Setting;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 설정 정보 응답 DTO
 */
@Getter
@Builder
public class SettingDto {
    private String id;
    private String key;
    private String value;
    private String description;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Setting 엔티티로부터 SettingDto 객체 생성
     * 
     * @param setting Setting 엔티티
     * @return SettingDto 객체
     */
    public static SettingDto fromEntity(Setting setting) {
        return SettingDto.builder()
                .id(setting.getId())
                .key(setting.getKey())
                .value(setting.getValue())
                .description(setting.getDescription())
                .category(setting.getCategory())
                .createdAt(setting.getCreatedAt())
                .updatedAt(setting.getUpdatedAt())
                .build();
    }
}
