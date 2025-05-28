package com.agi.plan.dto.response;

import com.agi.plan.enums.PlanStepStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanStepDto {
    private String id;
    private String planId;
    private String description;
    private Integer order;
    private PlanStepStatus status;
    private String toolId;
    private String toolExecutionId;
    private String expectedResult;
    private String actualResult;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
