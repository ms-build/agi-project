package com.agi.plan.repository;

import com.agi.plan.entity.PlanStep;
import com.agi.plan.entity.Plan;
import com.agi.plan.enums.PlanStepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanStepRepository extends JpaRepository<PlanStep, Long> {
    List<PlanStep> findByPlan(Plan plan);
    
    List<PlanStep> findByPlanOrderByStepNumberAsc(Plan plan);
    
    List<PlanStep> findByStatus(PlanStepStatus status);
    
    @Query("SELECT ps FROM PlanStep ps WHERE ps.plan.id = :planId ORDER BY ps.stepNumber ASC")
    List<PlanStep> findByPlanIdOrderByStepNumberAsc(String planId);
    
    @Query("SELECT ps FROM PlanStep ps WHERE ps.plan.id = :planId AND ps.status = :status")
    List<PlanStep> findByPlanIdAndStatus(String planId, PlanStepStatus status);
}
