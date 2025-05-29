package com.agi.multimodal.video.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

import com.agi.multimodal.video.enums.VideoFormat;
import com.agi.multimodal.common.entity.MediaTag;

@Entity
@Table(name = "video_metadata")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoMetadata {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String fileName;
    
    private String title;
    
    private String description;
    
    @Column(nullable = false)
    private String filePath;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoFormat format;
    
    private Integer duration;
    
    private String resolution;
    
    private Integer width;
    
    private Integer height;
    
    private Integer frameRate;
    
    private String codec;
    
    private Integer bitRate;
    
    @Column(nullable = false)
    private LocalDateTime uploadedAt;
    
    private LocalDateTime createdAt;
    
    private String userId;
    
    @ManyToMany
    private Set<MediaTag> tags = new HashSet<>();
}
