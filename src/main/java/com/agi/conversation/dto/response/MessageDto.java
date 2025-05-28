package com.agi.conversation.dto.response;

import com.agi.conversation.entity.Message;
import com.agi.conversation.enums.MessageType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 메시지 정보 응답 DTO
 */
@Getter
@Builder
public class MessageDto {
    private String id;
    private String conversationId;
    private String content;
    private MessageType type;
    private LocalDateTime createdAt;
    private String metadata;

    /**
     * Message 엔티티로부터 MessageDto 객체 생성
     * 
     * @param message Message 엔티티
     * @return MessageDto 객체
     */
    public static MessageDto fromEntity(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .content(message.getContent())
                .type(message.getType())
                .createdAt(message.getCreatedAt())
                .metadata(message.getMetadata())
                .build();
    }
}
