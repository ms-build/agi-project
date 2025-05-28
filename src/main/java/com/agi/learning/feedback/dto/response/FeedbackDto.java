package com.agi.learning.feedback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDto {
    private String id;
    private Long userId;
    private String content;
    private String targetType;
    private String targetId;
    private Integer rating;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
