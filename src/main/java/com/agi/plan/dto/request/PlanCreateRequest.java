package com.agi.plan.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanCreateRequest {
    
    @NotBlank(message = "계획 제목은 필수입니다")
    private String title;
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotNull(message = "대화 ID는 필수입니다")
    private String conversationId;
    
    private String description;
    
    private List<PlanStepRequest> steps;
}
