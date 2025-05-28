package com.agi.system.logging.dto.request;

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
public class LogRequest {
    
    @NotBlank(message = "로그 메시지는 필수입니다")
    private String message;
    
    @NotNull(message = "로그 레벨은 필수입니다")
    private String level;
    
    private String source;
    
    private String userId;
    
    private String sessionId;
    
    private String requestId;
    
    private LocalDateTime timestamp;
    
    private String stackTrace;
    
    private Object metadata;
}
