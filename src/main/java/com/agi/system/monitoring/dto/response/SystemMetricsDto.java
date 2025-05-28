package com.agi.system.monitoring.dto.response;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Map;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemMetricsDto {
    
    private String target;
    
    private Map<String, Object> metrics;
    
    private LocalDateTime collectedAt;
    
    private String interval;
    
    private Integer duration;
    
    private Boolean isSuccessful;
    
    private String errorMessage;
}
