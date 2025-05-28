package com.agi.plan.dto.response;

import com.agi.plan.entity.PlanStep;
import com.agi.plan.enums.PlanStepStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 계획 단계 정보 응답 DTO
 */
@Getter
@Builder
public class PlanStepDto {
    private String id;
    private String planId;
    private String content;
    private Integer order;
    private PlanStepStatus status;
    private String expectedResult;
    private String actualResult;
    private Integer estimatedDuration;
    private Integer actualDuration;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    /**
     * PlanStep 엔티티로부터 PlanStepDto 객체 생성
     * 
     * @param planStep PlanStep 엔티티
     * @return PlanStepDto 객체
     */
    public static PlanStepDto fromEntity(PlanStep planStep) {
        return PlanStepDto.builder()
                .id(planStep.getId())
                .planId(planStep.getPlan().getId())
                .content(planStep.getContent())
                .order(planStep.getOrder())
                .status(planStep.getStatus())
                .expectedResult(planStep.getExpectedResult())
                .actualResult(planStep.getActualResult())
                .estimatedDuration(planStep.getEstimatedDuration())
                .actualDuration(planStep.getActualDuration())
                .startedAt(planStep.getStartedAt())
                .completedAt(planStep.getCompletedAt())
                .build();
    }
}
