package com.agi.plan.dto.response;

import com.agi.plan.entity.Plan;
import com.agi.plan.enums.PlanStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 계획 정보 응답 DTO
 */
@Getter
@Builder
public class PlanDto {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private PlanStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PlanStepDto> steps;

    /**
     * Plan 엔티티로부터 PlanDto 객체 생성
     * 
     * @param plan Plan 엔티티
     * @return PlanDto 객체
     */
    public static PlanDto fromEntity(Plan plan) {
        return PlanDto.builder()
                .id(plan.getId())
                .userId(plan.getUser().getId())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .status(plan.getStatus())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .steps(plan.getSteps() != null ? 
                        plan.getSteps().stream()
                                .map(PlanStepDto::fromEntity)
                                .collect(Collectors.toList()) : 
                        null)
                .build();
    }
}
