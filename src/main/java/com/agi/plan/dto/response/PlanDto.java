package com.agi.plan.dto.response;

import com.agi.plan.enums.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDto {
    private String id;
    private String title;
    private String description;
    private Long userId;
    private String conversationId;
    private PlanStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private List<PlanStepDto> steps;
    private int currentStepIndex;
    private double progressPercentage;
}
