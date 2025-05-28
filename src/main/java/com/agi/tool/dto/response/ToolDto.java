package com.agi.tool.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 도구 정보 응답 DTO
 */
@Getter
@Builder
public class ToolDto {
    private String id;
    private String name;
    private String description;
    private String category;
    private List<ToolParameterDto> parameters;
    private boolean requiresSandbox;
    private boolean isBuiltIn;
}
