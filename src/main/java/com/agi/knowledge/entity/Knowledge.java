package com.agi.knowledge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 지식 엔티티
 */
@Entity
@Table(name = "knowledge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Knowledge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false)
    private String source;
    
    @Column(nullable = false)
    private Double relevanceScore;
    
    @Column(nullable = false)
    private Boolean verified;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "knowledge", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<KnowledgeTag> knowledgeTags = new HashSet<>();
    
    @Builder
    public Knowledge(String title, String content, String source, Double relevanceScore, Boolean verified) {
        this.title = title;
        this.content = content;
        this.source = source;
        this.relevanceScore = relevanceScore;
        this.verified = verified;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * 지식 내용 업데이트
     */
    public void updateContent(String title, String content, String source) {
        this.title = title;
        this.content = content;
        this.source = source;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 관련성 점수 업데이트
     */
    public void updateRelevanceScore(Double relevanceScore) {
        this.relevanceScore = relevanceScore;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 검증 상태 업데이트
     */
    public void updateVerified(Boolean verified) {
        this.verified = verified;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 태그 추가
     */
    public void addTag(KnowledgeTag tag) {
        this.knowledgeTags.add(tag);
    }
    
    /**
     * 태그 제거
     */
    public void removeTag(KnowledgeTag tag) {
        this.knowledgeTags.remove(tag);
    }
}
