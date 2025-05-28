package com.agi.knowledge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 지식 생성 요청 DTO
 */
@Getter
@Builder
public class KnowledgeCreateRequest {
    
    @NotBlank(message = "제목은 필수입니다")
    private String title;
    
    @NotBlank(message = "내용은 필수입니다")
    private String content;
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    private String source;
    
    private List<String> tags;
}
