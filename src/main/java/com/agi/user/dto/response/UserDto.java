package com.agi.user.dto.response;

import com.agi.user.entity.User;
import com.agi.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 사용자 정보 응답 DTO
 */
@Getter
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private Set<String> roles;

    /**
     * User 엔티티로부터 UserDto 객체 생성
     * 
     * @param user User 엔티티
     * @return UserDto 객체
     */
    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .roles(user.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getName())
                        .collect(Collectors.toSet()))
                .build();
    }
}
