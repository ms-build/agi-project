package com.agi.multimodal.common.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "media_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MediaTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(length = 255)
    private String description;
    
    @Column(name = "confidence_score")
    private Float confidenceScore;
    
    @Builder
    public MediaTag(String name, String description, Float confidenceScore) {
        this.name = name;
        this.description = description;
        this.confidenceScore = confidenceScore;
        this.createdAt = LocalDateTime.now();
    }
    
    public void updateName(String name) {
        this.name = name;
    }
    
    public void updateDescription(String description) {
        this.description = description;
    }
    
    public void updateConfidenceScore(Float confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
}
