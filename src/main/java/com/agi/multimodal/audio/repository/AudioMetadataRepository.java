package com.agi.multimodal.audio.repository;

import com.agi.multimodal.audio.entity.AudioMetadata;
import com.agi.multimodal.audio.enums.AudioFormat;
import com.agi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AudioMetadataRepository extends JpaRepository<AudioMetadata, String> {
    List<AudioMetadata> findByUser(User user);
    
    List<AudioMetadata> findByFormat(AudioFormat format);
    
    @Query("SELECT am FROM AudioMetadata am WHERE am.user.id = :userId")
    List<AudioMetadata> findByUserId(Long userId);
    
    @Query("SELECT am FROM AudioMetadata am WHERE am.uploadedAt >= :startDate AND am.uploadedAt <= :endDate")
    List<AudioMetadata> findByUploadedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT am FROM AudioMetadata am JOIN am.tags t WHERE t.name = :tagName")
    List<AudioMetadata> findByTagName(String tagName);
    
    @Query("SELECT am FROM AudioMetadata am WHERE am.filename LIKE %:keyword%")
    List<AudioMetadata> findByFilenameContaining(String keyword);
    
    @Query("SELECT am FROM AudioMetadata am WHERE am.duration >= :minDuration AND am.duration <= :maxDuration")
    List<AudioMetadata> findByDurationBetween(Integer minDuration, Integer maxDuration);
}
