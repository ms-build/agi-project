package com.agi.conversation.dto.response;

import com.agi.conversation.entity.Conversation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 대화 응답 DTO
 */
@Getter
@Builder
public class ConversationDto {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MessageDto> messages;
    
    /**
     * 엔티티에서 DTO로 변환
     */
    public static ConversationDto fromEntity(Conversation conversation) {
        return ConversationDto.builder()
                .id(conversation.getId())
                .userId(conversation.getUser().getId())
                .title(conversation.getTitle())
                .description(conversation.getDescription())
                .isActive(conversation.getIsActive())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }
    
    /**
     * 메시지 포함하여 엔티티에서 DTO로 변환
     */
    public static ConversationDto fromEntityWithMessages(Conversation conversation) {
        ConversationDto dto = fromEntity(conversation);
        if (conversation.getMessages() != null) {
            List<MessageDto> messageDtos = conversation.getMessages().stream()
                    .map(MessageDto::fromEntity)
                    .collect(Collectors.toList());
            dto.setMessages(messageDtos);
        }
        return dto;
    }
    
    // Lombok이 생성한 빌더에서는 불변 객체를 생성하므로, 메시지 목록을 설정하기 위한 별도 메서드 필요
    private void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }
}
