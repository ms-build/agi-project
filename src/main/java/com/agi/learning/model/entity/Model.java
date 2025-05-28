package com.agi.learning.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ElementCollection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

import com.agi.learning.model.enums.ModelType;

@Entity
@Table(name = "model")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Model {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModelType modelType;
    
    private String version;
    
    @Column(nullable = false)
    private String status;
    
    @ElementCollection
    private Map<String, Object> metrics = new HashMap<>();
    
    @ElementCollection
    private Map<String, Object> hyperParameters = new HashMap<>();
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime lastTrainedAt;
    
    private Long trainingTimeSeconds;
    
    private String datasetId;
    
    private Long parametersCount;
}
