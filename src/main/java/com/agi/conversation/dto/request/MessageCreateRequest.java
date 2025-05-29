package com.agi.conversation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 메시지 생성 요청 DTO
 */
@Getter
public class MessageCreateRequest {
    
    @NotNull(message = "대화 ID는 필수 입력값입니다")
    private Long conversationId;
    
    @NotBlank(message = "내용은 필수 입력값입니다")
    private String content;
    
    @NotBlank(message = "역할은 필수 입력값입니다")
    private String role;
}
