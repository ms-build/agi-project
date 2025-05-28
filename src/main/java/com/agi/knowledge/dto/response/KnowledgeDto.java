package com.agi.knowledge.dto.response;

import com.agi.knowledge.entity.Knowledge;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 지식 정보 응답 DTO
 */
@Getter
@Builder
public class KnowledgeDto {
    private String id;
    private String title;
    private String content;
    private Long userId;
    private String source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> tags;
    private Integer relevanceScore;

    /**
     * Knowledge 엔티티로부터 KnowledgeDto 객체 생성
     * 
     * @param knowledge Knowledge 엔티티
     * @return KnowledgeDto 객체
     */
    public static KnowledgeDto fromEntity(Knowledge knowledge) {
        return KnowledgeDto.builder()
                .id(knowledge.getId())
                .title(knowledge.getTitle())
                .content(knowledge.getContent())
                .userId(knowledge.getUser().getId())
                .source(knowledge.getSource())
                .createdAt(knowledge.getCreatedAt())
                .updatedAt(knowledge.getUpdatedAt())
                .tags(knowledge.getTags() != null ? 
                        knowledge.getTags().stream()
                                .map(tag -> tag.getName())
                                .collect(Collectors.toList()) : 
                        null)
                .build();
    }
}
