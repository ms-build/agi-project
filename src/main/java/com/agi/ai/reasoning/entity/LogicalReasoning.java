package com.agi.ai.reasoning.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

import com.agi.ai.reasoning.enums.ReasoningType;

@Entity
@Table(name = "logical_reasoning")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogicalReasoning {
    
    @Id
    private String id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    
    @Column(columnDefinition = "TEXT")
    private String context;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String conclusion;
    
    @ElementCollection
    @CollectionTable(name = "logical_reasoning_steps", joinColumns = @JoinColumn(name = "logical_reasoning_id"))
    @Column(name = "step", columnDefinition = "TEXT")
    private List<String> reasoningSteps;
    
    @Column(columnDefinition = "TEXT")
    private String explanation;
    
    private Double confidenceScore;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReasoningType reasoningType;
    
    private Boolean isValid;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
