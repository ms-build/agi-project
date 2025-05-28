package com.agi.ai.generation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

import com.agi.ai.generation.enums.GenerationModelType;

@Entity
@Table(name = "text_generation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextGeneration {
    
    @Id
    private String id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String prompt;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String generatedText;
    
    private Integer tokensUsed;
    
    private Double completionTime;
    
    @Enumerated(EnumType.STRING)
    private GenerationModelType modelType;
    
    private Double temperature;
    
    private Integer maxTokens;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private Boolean isSuccessful;
}
