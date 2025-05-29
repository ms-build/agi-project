package com.agi.ai.nlp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

import com.agi.ai.nlp.enums.IntentType;
import com.agi.user.entity.User;
import com.agi.conversation.entity.Conversation;

@Entity
@Table(name = "intent")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Intent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntentType primaryIntent;
    
    @Column(columnDefinition = "JSON")
    private String intentScores;
    
    @Column(columnDefinition = "JSON")
    private String entities;
    
    private String sentiment;
    
    private Double sentimentScore;
    
    @Column(nullable = false)
    private LocalDateTime analyzedAt;
    
    private String modelVersion;
    
    // DTO 변환에 필요한 추가 메서드
    public Double getConfidence() {
        return 0.95; // 기본값 제공 또는 intentScores에서 계산
    }
    
    // 타입 변환 메서드
    public String getPrimaryIntentAsString() {
        return primaryIntent != null ? primaryIntent.name() : null;
    }
}
