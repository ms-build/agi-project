package com.agi.conversation.dto.response;

import com.agi.conversation.entity.Conversation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 대화 정보 응답 DTO
 */
@Getter
@Builder
public class ConversationDto {
    private String id;
    private Long userId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    private Integer messageCount;

    /**
     * Conversation 엔티티로부터 ConversationDto 객체 생성
     * 
     * @param conversation Conversation 엔티티
     * @return ConversationDto 객체
     */
    public static ConversationDto fromEntity(Conversation conversation) {
        return ConversationDto.builder()
                .id(conversation.getId())
                .userId(conversation.getUser().getId())
                .title(conversation.getTitle())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .status(conversation.getStatus())
                .messageCount(conversation.getMessages() != null ? conversation.getMessages().size() : 0)
                .build();
    }
}
