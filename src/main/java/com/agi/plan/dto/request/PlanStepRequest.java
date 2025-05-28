package com.agi.plan.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * 계획 단계 요청 DTO
 */
@Getter
@Builder
public class PlanStepRequest {
    
    @NotBlank(message = "단계 내용은 필수입니다")
    private String content;
    
    @NotNull(message = "순서는 필수입니다")
    private Integer order;
    
    private String expectedResult;
    
    private Integer estimatedDuration;
}
