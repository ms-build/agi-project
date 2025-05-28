package com.agi.tool.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 도구 파라미터 정보 응답 DTO
 */
@Getter
@Builder
public class ToolParameterDto {
    private String name;
    private String type;
    private String description;
    private boolean required;
    private Object defaultValue;
}
