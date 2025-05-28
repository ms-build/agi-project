package com.agi.sandbox.dto.response;

import com.agi.sandbox.entity.Sandbox;
import com.agi.sandbox.enums.SandboxStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 샌드박스 정보 응답 DTO
 */
@Getter
@Builder
public class SandboxDto {
    private String id;
    private Long userId;
    private String name;
    private String description;
    private SandboxStatus status;
    private String configuration;
    private Integer timeoutSeconds;
    private Integer memoryLimitMb;
    private Integer cpuLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastAccessedAt;

    /**
     * Sandbox 엔티티로부터 SandboxDto 객체 생성
     * 
     * @param sandbox Sandbox 엔티티
     * @return SandboxDto 객체
     */
    public static SandboxDto fromEntity(Sandbox sandbox) {
        return SandboxDto.builder()
                .id(sandbox.getId())
                .userId(sandbox.getUser().getId())
                .name(sandbox.getName())
                .description(sandbox.getDescription())
                .status(sandbox.getStatus())
                .configuration(sandbox.getConfiguration())
                .timeoutSeconds(sandbox.getTimeoutSeconds())
                .memoryLimitMb(sandbox.getMemoryLimitMb())
                .cpuLimit(sandbox.getCpuLimit())
                .createdAt(sandbox.getCreatedAt())
                .updatedAt(sandbox.getUpdatedAt())
                .lastAccessedAt(sandbox.getLastAccessedAt())
                .build();
    }
}
