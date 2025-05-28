package com.agi.sandbox.dto.request;

import com.agi.sandbox.enums.SandboxStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * 샌드박스 생성 요청 DTO
 */
@Getter
@Builder
public class SandboxCreateRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotBlank(message = "샌드박스 이름은 필수입니다")
    private String name;
    
    private String description;
    
    private String configuration;
    
    private Integer timeoutSeconds;
    
    private Integer memoryLimitMb;
    
    private Integer cpuLimit;
}
