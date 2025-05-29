package com.agi.learning.feedback.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

import com.agi.learning.feedback.entity.Feedback;
import com.agi.learning.feedback.enums.FeedbackCategory;

@Getter
@Builder
public class FeedbackDto {
    
    private Long id;
    private Long userId;
    private Long conversationId;
    private FeedbackCategory category;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
    private String messageId;
    private String context;
    
    public static FeedbackDto fromEntity(Feedback feedback) {
        return FeedbackDto.builder()
                .id(feedback.getId())
                .userId(feedback.getUser() != null ? feedback.getUser().getId() : null)
                .conversationId(feedback.getConversation() != null ? feedback.getConversation().getId() : null)
                .category(feedback.getCategory())
                .score(feedback.getScore())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt())
                .messageId(feedback.getMessageId())
                .context(feedback.getContext())
                .build();
    }
}
