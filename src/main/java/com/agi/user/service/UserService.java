package com.agi.user.service;

import com.agi.user.dto.request.PasswordChangeRequest;
import com.agi.user.dto.request.UserCreateRequest;
import com.agi.user.dto.request.UserUpdateRequest;
import com.agi.user.dto.response.UserDto;

import java.util.List;

/**
 * 사용자 관리 서비스 인터페이스
 */
public interface UserService {
    /**
     * ID로 사용자 조회
     * @param id 사용자 ID
     * @return 사용자 DTO
     */
    UserDto getUserById(Long id);
    
    /**
     * 사용자명으로 사용자 조회
     * @param username 사용자명
     * @return 사용자 DTO
     */
    UserDto getUserByUsername(String username);
    
    /**
     * 이메일로 사용자 조회
     * @param email 이메일
     * @return 사용자 DTO
     */
    UserDto getUserByEmail(String email);
    
    /**
     * 모든 사용자 조회
     * @return 사용자 DTO 목록
     */
    List<UserDto> getAllUsers();
    
    /**
     * 사용자 생성
     * @param request 사용자 생성 요청
     * @return 생성된 사용자 DTO
     */
    UserDto createUser(UserCreateRequest request);
    
    /**
     * 사용자 정보 수정
     * @param id 사용자 ID
     * @param request 사용자 수정 요청
     * @return 수정된 사용자 DTO
     */
    UserDto updateUser(Long id, UserUpdateRequest request);
    
    /**
     * 사용자 삭제
     * @param id 사용자 ID
     */
    void deleteUser(Long id);
    
    /**
     * 비밀번호 변경
     * @param id 사용자 ID
     * @param request 비밀번호 변경 요청
     */
    void changePassword(Long id, PasswordChangeRequest request);
    
    /**
     * 사용자 활성화/비활성화
     * @param id 사용자 ID
     * @param active 활성화 여부
     * @return 수정된 사용자 DTO
     */
    UserDto setUserActiveStatus(Long id, boolean active);
    
    /**
     * 사용자에게 역할 추가
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return 수정된 사용자 DTO
     */
    UserDto addRoleToUser(Long userId, Long roleId);
    
    /**
     * 사용자에서 역할 제거
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return 수정된 사용자 DTO
     */
    UserDto removeRoleFromUser(Long userId, Long roleId);
}
