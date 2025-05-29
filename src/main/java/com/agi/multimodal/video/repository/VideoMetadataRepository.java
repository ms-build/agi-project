package com.agi.multimodal.video.repository;

import com.agi.multimodal.video.entity.VideoMetadata;
import com.agi.multimodal.video.enums.VideoFormat;
import com.agi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VideoMetadataRepository extends JpaRepository<VideoMetadata, String> {
    List<VideoMetadata> findByUser(User user);
    
    List<VideoMetadata> findByFormat(VideoFormat format);
    
    @Query("SELECT vm FROM VideoMetadata vm WHERE vm.user.id = :userId")
    List<VideoMetadata> findByUserId(Long userId);
    
    @Query("SELECT vm FROM VideoMetadata vm WHERE vm.uploadedAt >= :startDate AND vm.uploadedAt <= :endDate")
    List<VideoMetadata> findByUploadedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT vm FROM VideoMetadata vm JOIN vm.tags t WHERE t.name = :tagName")
    List<VideoMetadata> findByTagName(String tagName);
    
    @Query("SELECT vm FROM VideoMetadata vm WHERE vm.filename LIKE %:keyword%")
    List<VideoMetadata> findByFilenameContaining(String keyword);
    
    @Query("SELECT vm FROM VideoMetadata vm WHERE vm.duration >= :minDuration AND vm.duration <= :maxDuration")
    List<VideoMetadata> findByDurationBetween(Integer minDuration, Integer maxDuration);
    
    @Query("SELECT vm FROM VideoMetadata vm WHERE vm.resolution = :resolution")
    List<VideoMetadata> findByResolution(String resolution);
}
