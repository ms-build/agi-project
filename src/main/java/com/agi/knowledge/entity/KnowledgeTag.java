package com.agi.knowledge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 지식 태그 엔티티
 */
@Entity
@Table(name = "knowledge_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KnowledgeTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "knowledge_id")
    private Knowledge knowledge;
    
    @Column(nullable = false)
    private String tag;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Builder
    public KnowledgeTag(Knowledge knowledge, String tag) {
        this.knowledge = knowledge;
        this.tag = tag;
        this.createdAt = LocalDateTime.now();
    }
}
