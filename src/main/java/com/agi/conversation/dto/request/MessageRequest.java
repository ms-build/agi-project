package com.agi.conversation.dto.request;

import com.agi.conversation.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * 메시지 전송 요청 DTO
 */
@Getter
@Builder
public class MessageRequest {
    
    @NotNull(message = "대화 ID는 필수입니다")
    private String conversationId;
    
    @NotBlank(message = "메시지 내용은 필수입니다")
    private String content;
    
    @NotNull(message = "메시지 타입은 필수입니다")
    private MessageType type;
    
    private String metadata;
}
