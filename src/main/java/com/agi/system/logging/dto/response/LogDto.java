package com.agi.system.logging.dto.response;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogDto {
    
    private String id;
    
    private String message;
    
    private String level;
    
    private String source;
    
    private String userId;
    
    private String sessionId;
    
    private String requestId;
    
    private LocalDateTime timestamp;
    
    private String stackTrace;
    
    private Object metadata;
}
