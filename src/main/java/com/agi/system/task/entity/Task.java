package com.agi.system.task.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

import com.agi.system.task.enums.TaskStatus;

@Entity
@Table(name = "task")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String type;
    
    private String description;
    
    private String userId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime scheduledAt;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    private Integer priority;
    
    @Column(columnDefinition = "JSON")
    private String parameters;
    
    @Column(columnDefinition = "JSON")
    private String result;
    
    private String errorMessage;
    
    private String parentTaskId;
    
    private Double progress;
}
