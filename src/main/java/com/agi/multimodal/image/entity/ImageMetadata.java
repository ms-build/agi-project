package com.agi.multimodal.image.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

import com.agi.multimodal.image.enums.ImageFormat;
import com.agi.multimodal.common.entity.MediaTag;
import com.agi.user.entity.User;

@Entity
@Table(name = "image_metadata")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageMetadata {
    
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
    private ImageFormat format;
    
    private Integer width;
    
    private Integer height;
    
    private String resolution;
    
    private String colorSpace;
    
    private Boolean hasAlphaChannel;
    
    @Column(nullable = false)
    private LocalDateTime uploadedAt;
    
    private LocalDateTime createdAt;
    
    @ManyToMany
    private Set<MediaTag> tags = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // DTO 변환에 필요한 추가 메서드
    public String getFilename() {
        return fileName;
    }
    
    public String getUrl() {
        return filePath;
    }
    
    public Long getSize() {
        return fileSize;
    }
}
