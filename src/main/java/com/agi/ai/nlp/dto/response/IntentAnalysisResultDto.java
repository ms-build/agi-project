package com.agi.ai.nlp.dto.response;

import com.agi.ai.nlp.entity.Intent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 의도 분석 결과 응답 DTO
 */
@Getter
@Builder
public class IntentAnalysisResultDto {
    private String id;
    private String conversationId;
    private Long userId;
    private String text;
    private String primaryIntent;
    private Double confidence;
    private Map<String, Double> intentScores;
    private Map<String, Object> entities;
    private String sentiment;
    private Double sentimentScore;
    private LocalDateTime analyzedAt;

    /**
     * Intent 엔티티로부터 IntentAnalysisResultDto 객체 생성
     * 
     * @param intent Intent 엔티티
     * @return IntentAnalysisResultDto 객체
     */
    public static IntentAnalysisResultDto fromEntity(Intent intent) {
        return IntentAnalysisResultDto.builder()
                .id(intent.getId())
                .conversationId(intent.getConversation().getId())
                .userId(intent.getUser() != null ? intent.getUser().getId() : null)
                .text(intent.getText())
                .primaryIntent(intent.getPrimaryIntent())
                .confidence(intent.getConfidence())
                .intentScores(intent.getIntentScores())
                .entities(intent.getEntities())
                .sentiment(intent.getSentiment())
                .sentimentScore(intent.getSentimentScore())
                .analyzedAt(intent.getAnalyzedAt())
                .build();
    }
}
