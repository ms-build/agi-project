package com.agi.knowledge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "knowledge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Knowledge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(nullable = false)
    private String source;
    
    @Column(nullable = false)
    private Double confidence;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @Column(columnDefinition = "JSON")
    private String metadata;
    
    @ManyToMany
    @JoinTable(
        name = "knowledge_tags",
        joinColumns = @JoinColumn(name = "knowledge_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<KnowledgeTag> tags = new HashSet<>();
    
    @Builder
    public Knowledge(String title, String content, String source, Double confidence) {
        this.title = title;
        this.content = content;
        this.source = source;
        this.confidence = confidence;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    
    public void update(String title, String content, String source, Double confidence) {
        this.title = title;
        this.content = content;
        this.source = source;
        this.confidence = confidence;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateMetadata(String metadata) {
        this.metadata = metadata;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addTag(KnowledgeTag tag) {
        this.tags.add(tag);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void removeTag(KnowledgeTag tag) {
        this.tags.remove(tag);
        this.updatedAt = LocalDateTime.now();
    }
}
