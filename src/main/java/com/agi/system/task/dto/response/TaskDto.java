package com.agi.system.task.dto.response;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    
    private String id;
    
    private String name;
    
    private String type;
    
    private String description;
    
    private String userId;
    
    private String status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime scheduledAt;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    private Integer priority;
    
    private Object parameters;
    
    private Object result;
    
    private String errorMessage;
    
    private String parentTaskId;
    
    private Double progress;
}
