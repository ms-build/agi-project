package com.agi.system.setting.repository;

import com.agi.system.setting.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 설정 저장소 인터페이스
 */
public interface SettingRepository extends JpaRepository<Setting, Long> {
    
    /**
     * 카테고리별 설정 조회
     * @param category 설정 카테고리
     * @return 설정 목록
     */
    List<Setting> findByCategory(String category);
    
    /**
     * 키로 설정 조회
     * @param key 설정 키
     * @return 설정
     */
    Optional<Setting> findByKey(String key);
    
    /**
     * 카테고리와 키로 설정 조회
     * @param category 설정 카테고리
     * @param key 설정 키
     * @return 설정
     */
    Optional<Setting> findByCategoryAndKey(String category, String key);
    
    /**
     * 활성화된 설정 조회
     * @return 활성화된 설정 목록
     */
    List<Setting> findByActiveTrue();
    
    /**
     * 카테고리별 활성화된 설정 조회
     * @param category 설정 카테고리
     * @return 활성화된 설정 목록
     */
    List<Setting> findByCategoryAndActiveTrue(String category);
    
    /**
     * 설정 존재 여부 확인
     * @param key 설정 키
     * @return 존재 여부
     */
    boolean existsByKey(String key);
    
    /**
     * 카테고리별 설정 개수 조회
     * @param category 설정 카테고리
     * @return 설정 개수
     */
    @Query("SELECT COUNT(s) FROM Setting s WHERE s.category = :category")
    long countByCategory(@Param("category") String category);
}
