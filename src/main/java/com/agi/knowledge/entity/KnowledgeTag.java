package com.agi.knowledge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KnowledgeTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "knowledge_id", nullable = false)
    private Knowledge knowledge;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Builder
    public KnowledgeTag(Knowledge knowledge, String name) {
        this.knowledge = knowledge;
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }
    
    public void updateName(String name) {
        this.name = name;
    }
}
