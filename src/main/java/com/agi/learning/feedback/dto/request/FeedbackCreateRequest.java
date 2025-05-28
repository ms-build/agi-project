package com.agi.learning.feedback.dto.request;

import com.agi.learning.feedback.enums.FeedbackCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * 피드백 생성 요청 DTO
 */
@Getter
@Builder
public class FeedbackCreateRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotNull(message = "대화 ID는 필수입니다")
    private String conversationId;
    
    @NotNull(message = "피드백 카테고리는 필수입니다")
    private FeedbackCategory category;
    
    @NotNull(message = "점수는 필수입니다")
    @Min(value = 1, message = "점수는 1점 이상이어야 합니다")
    @Max(value = 5, message = "점수는 5점 이하여야 합니다")
    private Integer score;
    
    private String comment;
}
