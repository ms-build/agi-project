package com.agi.user.enums;

/**
 * 사용자 역할 열거형
 */
public enum UserRoleType {
    ADMIN("관리자"),
    USER("일반 사용자"),
    DEVELOPER("개발자"),
    ANALYST("분석가"),
    GUEST("게스트");
    
    private final String description;
    
    UserRoleType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
