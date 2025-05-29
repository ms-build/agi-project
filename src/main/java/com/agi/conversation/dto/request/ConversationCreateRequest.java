package com.agi.conversation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 대화 생성 요청 DTO
 */
@Getter
public class ConversationCreateRequest {
    
    @NotNull(message = "사용자 ID는 필수 입력값입니다")
    private Long userId;
    
    @NotBlank(message = "제목은 필수 입력값입니다")
    private String title;
    
    private String description;
}
