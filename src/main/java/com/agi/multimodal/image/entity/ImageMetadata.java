package com.agi.multimodal.image.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "image_metadata")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String imageId;
    
    @Column(nullable = false)
    private String filename;
    
    @Column(nullable = false)
    private String contentType;
    
    @Column(nullable = false)
    private Long fileSize;
    
    private Integer width;
    
    private Integer height;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "JSON")
    private String detectionResults;
    
    @Column(columnDefinition = "JSON")
    private String metadata;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @ManyToMany
    @JoinTable(
        name = "image_tags",
        joinColumns = @JoinColumn(name = "image_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<MediaTag> tags = new HashSet<>();
    
    @Builder
    public ImageMetadata(String imageId, String filename, String contentType, Long fileSize, 
                         Integer width, Integer height, String description) {
        this.imageId = imageId;
        this.filename = filename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.width = width;
        this.height = height;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    
    public void updateDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateDetectionResults(String detectionResults) {
        this.detectionResults = detectionResults;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateMetadata(String metadata) {
        this.metadata = metadata;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addTag(MediaTag tag) {
        this.tags.add(tag);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void removeTag(MediaTag tag) {
        this.tags.remove(tag);
        this.updatedAt = LocalDateTime.now();
    }
}
