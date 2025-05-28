package com.agi.user.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * JWT 토큰 응답 DTO
 */
@Getter
@Builder
public class JwtTokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
}
