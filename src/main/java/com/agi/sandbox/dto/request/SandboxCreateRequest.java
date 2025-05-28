package com.agi.sandbox.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SandboxCreateRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotBlank(message = "샌드박스 이름은 필수입니다")
    private String name;
    
    private String description;
    
    private String templateId;
    
    private Map<String, Object> configuration;
    
    private Integer timeoutSeconds;
}
