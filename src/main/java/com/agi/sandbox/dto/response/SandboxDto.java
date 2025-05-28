package com.agi.sandbox.dto.response;

import com.agi.sandbox.enums.SandboxStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SandboxDto {
    private String id;
    private Long userId;
    private String name;
    private String description;
    private String templateId;
    private SandboxStatus status;
    private Map<String, Object> configuration;
    private Map<String, Object> resources;
    private Integer timeoutSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime terminatedAt;
}
