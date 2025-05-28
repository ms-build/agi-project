package com.agi.plan.entity;

import com.agi.plan.enums.PlanStepStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plan_step")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
    
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStepStatus status;
    
    @Column(name = "depends_on")
    private String dependsOn;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime completedAt;
    
    @Column(columnDefinition = "TEXT")
    private String result;
    
    @ElementCollection
    @CollectionTable(
        name = "plan_step_dependency",
        joinColumns = @JoinColumn(name = "step_id")
    )
    @Column(name = "dependency_step_id")
    private List<Long> dependencies = new ArrayList<>();
    
    @Builder
    public PlanStep(Plan plan, Integer orderIndex, String description, String dependsOn) {
        this.plan = plan;
        this.orderIndex = orderIndex;
        this.description = description;
        this.dependsOn = dependsOn;
        this.status = PlanStepStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    public void updateStatus(PlanStepStatus status) {
        this.status = status;
        if (status == PlanStepStatus.COMPLETED) {
            this.completedAt = LocalDateTime.now();
        }
    }
    
    public void updateResult(String result) {
        this.result = result;
    }
    
    public void addDependency(Long dependencyStepId) {
        this.dependencies.add(dependencyStepId);
    }
    
    public void removeDependency(Long dependencyStepId) {
        this.dependencies.remove(dependencyStepId);
    }
}
