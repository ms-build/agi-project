package com.agi.ai.nlp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "intent")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Intent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @Column(length = 200)
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String trainingPhrases;
    
    @Column(nullable = false)
    private Double confidence;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @Column(columnDefinition = "JSON")
    private String parameters;
    
    @Builder
    public Intent(String name, String description, String trainingPhrases, Double confidence) {
        this.name = name;
        this.description = description;
        this.trainingPhrases = trainingPhrases;
        this.confidence = confidence;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    
    public void update(String description, String trainingPhrases, Double confidence) {
        this.description = description;
        this.trainingPhrases = trainingPhrases;
        this.confidence = confidence;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateParameters(String parameters) {
        this.parameters = parameters;
        this.updatedAt = LocalDateTime.now();
    }
}
