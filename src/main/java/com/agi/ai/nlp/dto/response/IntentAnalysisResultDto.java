package com.agi.ai.nlp.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Map;

import com.agi.ai.nlp.entity.Intent;

@Getter
@Builder
public class IntentAnalysisResultDto {
    
    private Long id;
    private Long conversationId;
    private Long userId;
    private String text;
    private String primaryIntent;
    private Double confidence;
    private Map<String, Double> intentScores;
    private Map<String, Object> entities;
    private String sentiment;
    private Double sentimentScore;
    private LocalDateTime analyzedAt;
    
    public static IntentAnalysisResultDto fromEntity(Intent intent) {
        return IntentAnalysisResultDto.builder()
                .id(intent.getId())
                .conversationId(intent.getConversation() != null ? intent.getConversation().getId() : null)
                .userId(intent.getUser() != null ? intent.getUser().getId() : null)
                .text(intent.getText())
                .primaryIntent(intent.getPrimaryIntentAsString())
                .confidence(intent.getConfidence())
                .intentScores(null) // JSON 파싱 필요
                .entities(null) // JSON 파싱 필요
                .sentiment(intent.getSentiment())
                .sentimentScore(intent.getSentimentScore())
                .analyzedAt(intent.getAnalyzedAt())
                .build();
    }
}
