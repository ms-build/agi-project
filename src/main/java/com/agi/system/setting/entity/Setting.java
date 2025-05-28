package com.agi.system.setting.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "setting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String key;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String value;
    
    @Column(length = 200)
    private String description;
    
    @Column(nullable = false)
    private String dataType;
    
    @Column(nullable = false)
    private boolean isSystem;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @Builder
    public Setting(String key, String value, String description, String dataType, boolean isSystem) {
        this.key = key;
        this.value = value;
        this.description = description;
        this.dataType = dataType;
        this.isSystem = isSystem;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    
    public void updateValue(String value) {
        this.value = value;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
}
