package com.agi.system.task.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    
    @NotBlank(message = "작업 이름은 필수입니다")
    private String name;
    
    @NotNull(message = "작업 유형은 필수입니다")
    private String type;
    
    private String description;
    
    private String userId;
    
    private LocalDateTime scheduledAt;
    
    private Integer priority;
    
    private Object parameters;
    
    private String parentTaskId;
}
