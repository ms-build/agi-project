package com.agi.system.setting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "setting")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setting {
    
    @Id
    private String id;
    
    @Column(nullable = false, unique = true)
    private String key;
    
    @Column(nullable = false)
    private String value;
    
    private String description;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private Boolean isSystem;
    
    private String dataType;
    
    private String defaultValue;
    
    private String validationRules;
}
