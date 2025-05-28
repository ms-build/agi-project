package com.agi.learning.feedback.dto.response;

import com.agi.learning.feedback.entity.Feedback;
import com.agi.learning.feedback.enums.FeedbackCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 피드백 정보 응답 DTO
 */
@Getter
@Builder
public class FeedbackDto {
    private String id;
    private Long userId;
    private String conversationId;
    private FeedbackCategory category;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;

    /**
     * Feedback 엔티티로부터 FeedbackDto 객체 생성
     * 
     * @param feedback Feedback 엔티티
     * @return FeedbackDto 객체
     */
    public static FeedbackDto fromEntity(Feedback feedback) {
        return FeedbackDto.builder()
                .id(feedback.getId())
                .userId(feedback.getUser().getId())
                .conversationId(feedback.getConversation().getId())
                .category(feedback.getCategory())
                .score(feedback.getScore())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}
