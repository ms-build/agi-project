package com.agi.ai.nlp.entity;

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

import com.agi.ai.nlp.enums.SentimentType;

@Entity
@Table(name = "sentiment_analysis")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SentimentAnalysis {
    
    @Id
    private String id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SentimentType sentiment;
    
    private Double positiveScore;
    
    private Double negativeScore;
    
    private Double neutralScore;
    
    private String language;
    
    @Column(nullable = false)
    private LocalDateTime analyzedAt;
}
