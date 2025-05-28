package com.agi.plan.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 계획 생성 요청 DTO
 */
@Getter
@Builder
public class PlanCreateRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotBlank(message = "제목은 필수입니다")
    private String title;
    
    private String description;
    
    private List<PlanStepRequest> steps;
}
