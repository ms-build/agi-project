package com.agi.plan.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanStepRequest {
    
    @NotBlank(message = "단계 설명은 필수입니다")
    private String description;
    
    @NotNull(message = "단계 순서는 필수입니다")
    private Integer order;
    
    private String toolId;
    
    private String expectedResult;
}
