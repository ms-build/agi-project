package com.agi.tool.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * 도구 실행 요청 DTO
 */
@Getter
@Builder
public class ToolExecuteRequest {
    
    @NotBlank(message = "도구 이름은 필수입니다")
    private String toolName;
    
    @NotNull(message = "파라미터는 필수입니다")
    private Map<String, Object> parameters;
    
    private String sandboxId;
    
    private Integer timeout;
}
