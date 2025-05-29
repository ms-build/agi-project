package com.agi.multimodal.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

import com.agi.multimodal.audio.entity.AudioMetadata;
import com.agi.multimodal.image.entity.ImageMetadata;
import com.agi.multimodal.video.entity.VideoMetadata;

@Entity
@Table(name = "media_tag")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaTag {
    
    @Id
    private String id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @ManyToMany(mappedBy = "tags")
    private Set<ImageMetadata> images = new HashSet<>();
    
    @ManyToMany(mappedBy = "tags")
    private Set<AudioMetadata> audios = new HashSet<>();
    
    @ManyToMany(mappedBy = "tags")
    private Set<VideoMetadata> videos = new HashSet<>();
}
