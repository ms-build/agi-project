package com.agi.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 정보 수정 요청 DTO
 */
@Getter
@Builder
public class UserUpdateRequest {
    
    @NotBlank(message = "닉네임은 필수입니다")
    private String nickname;
    
    private String profileImageUrl;
    
    private String bio;
}
