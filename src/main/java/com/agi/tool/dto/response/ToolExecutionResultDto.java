package com.agi.tool.dto.response;

import com.agi.tool.enums.ToolStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 도구 실행 결과 응답 DTO
 */
@Getter
@Builder
public class ToolExecutionResultDto {
    private String executionId;
    private String toolName;
    private ToolStatus status;
    private Map<String, Object> result;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorMessage;
}
