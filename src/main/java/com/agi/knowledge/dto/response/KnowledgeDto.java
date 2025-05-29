package com.agi.knowledge.dto.response;

import com.agi.knowledge.entity.Knowledge;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 지식 응답 DTO
 */
@Getter
@Builder
public class KnowledgeDto {
    private Long id;
    private String title;
    private String content;
    private String source;
    private Double relevanceScore;
    private Boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> tags;
    
    /**
     * 엔티티에서 DTO로 변환
     */
    public static KnowledgeDto fromEntity(Knowledge knowledge) {
        return KnowledgeDto.builder()
                .id(knowledge.getId())
                .title(knowledge.getTitle())
                .content(knowledge.getContent())
                .source(knowledge.getSource())
                .relevanceScore(knowledge.getRelevanceScore())
                .verified(knowledge.getVerified())
                .createdAt(knowledge.getCreatedAt())
                .updatedAt(knowledge.getUpdatedAt())
                .tags(knowledge.getKnowledgeTags().stream()
                        .map(tag -> tag.getTag())
                        .collect(Collectors.toSet()))
                .build();
    }
}
