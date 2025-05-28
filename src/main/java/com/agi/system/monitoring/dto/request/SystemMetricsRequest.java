package com.agi.system.monitoring.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemMetricsRequest {
    
    @NotBlank(message = "모니터링 대상은 필수입니다")
    private String target;
    
    private String[] metrics;
    
    private Integer duration;
    
    private String interval;
}
