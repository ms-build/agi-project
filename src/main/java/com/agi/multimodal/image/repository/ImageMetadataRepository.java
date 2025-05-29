package com.agi.multimodal.image.repository;

import com.agi.multimodal.image.entity.ImageMetadata;
import com.agi.multimodal.image.enums.ImageFormat;
import com.agi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ImageMetadataRepository extends JpaRepository<ImageMetadata, String> {
    List<ImageMetadata> findByUser(User user);
    
    List<ImageMetadata> findByFormat(ImageFormat format);
    
    @Query("SELECT im FROM ImageMetadata im WHERE im.user.id = :userId")
    List<ImageMetadata> findByUserId(Long userId);
    
    @Query("SELECT im FROM ImageMetadata im WHERE im.uploadedAt >= :startDate AND im.uploadedAt <= :endDate")
    List<ImageMetadata> findByUploadedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT im FROM ImageMetadata im JOIN im.tags t WHERE t.name = :tagName")
    List<ImageMetadata> findByTagName(String tagName);
    
    @Query("SELECT im FROM ImageMetadata im WHERE im.filename LIKE %:keyword%")
    List<ImageMetadata> findByFilenameContaining(String keyword);
}
