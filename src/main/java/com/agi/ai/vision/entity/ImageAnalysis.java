package com.agi.ai.vision.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "image_analysis")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageAnalysis {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String imageUrl;
    
    @ElementCollection
    @CollectionTable(name = "image_analysis_labels", joinColumns = @JoinColumn(name = "image_analysis_id"))
    @Column(name = "label")
    private List<String> labels;
    
    @Column(columnDefinition = "TEXT")
    private String detectedObjectsJson;
    
    @Column(columnDefinition = "TEXT")
    private String detectedFacesJson;
    
    private String dominantColor;
    
    @Column(nullable = false)
    private LocalDateTime analyzedAt;
    
    private Boolean isSuccessful;
}
