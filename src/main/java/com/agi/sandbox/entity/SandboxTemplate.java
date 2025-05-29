package com.agi.sandbox.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "sandbox_template")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SandboxTemplate {
    
    @Id
    private String id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private Boolean isActive;
    
    private String baseImage;
    
    private String configuration;
    
    private Integer defaultCpu;
    
    private Integer defaultMemory;
    
    private Integer defaultDisk;
    
    private String networkConfig;
    
    private String preInstalledPackages;
}
