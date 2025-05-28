package com.agi.knowledge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeSearchRequest {
    
    @NotBlank(message = "검색어는 필수입니다")
    private String query;
    
    private Integer limit;
    
    private Double minRelevanceScore;
    
    private String tag;
    
    private String source;
}
