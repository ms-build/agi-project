package com.agi.conversation.dto.response;

import com.agi.conversation.entity.Message;
import com.agi.conversation.enums.MessageType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 메시지 응답 DTO
 */
@Getter
@Builder
public class MessageDto {
    private Long id;
    private Long conversationId;
    private String content;
    private MessageType role;
    private LocalDateTime createdAt;
    
    /**
     * 엔티티에서 DTO로 변환
     */
    public static MessageDto fromEntity(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .content(message.getContent())
                .role(message.getRole())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
