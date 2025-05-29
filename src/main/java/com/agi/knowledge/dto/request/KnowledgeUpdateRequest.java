package com.agi.knowledge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 지식 수정 요청 DTO
 */
@Getter
public class KnowledgeUpdateRequest {
    
    @NotBlank(message = "제목은 필수 입력값입니다")
    private String title;
    
    @NotBlank(message = "내용은 필수 입력값입니다")
    private String content;
    
    @NotBlank(message = "출처는 필수 입력값입니다")
    private String source;
    
    @NotNull(message = "관련성 점수는 필수 입력값입니다")
    private Double relevanceScore;
    
    private Boolean verified;
}
