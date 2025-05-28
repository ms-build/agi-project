package com.agi.knowledge.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 지식 검색 요청 DTO
 */
@Getter
@Builder
public class KnowledgeSearchRequest {
    
    private String keyword;
    
    private List<String> tags;
    
    private String source;
    
    private Long userId;
    
    private String sortBy;
    
    private String sortDirection;
    
    private Integer page;
    
    private Integer size;
}
