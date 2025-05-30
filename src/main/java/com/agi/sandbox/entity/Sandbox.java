package com.agi.sandbox.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

import com.agi.sandbox.enums.SandboxStatus;
import com.agi.user.entity.User;

@Entity
@Table(name = "sandbox")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sandbox {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SandboxStatus status;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime lastAccessedAt;
    
    private LocalDateTime updatedAt;
    
    private String templateId;
    
    private String configuration;
    
    private Integer cpuLimit;
    
    private Integer memoryLimitMb;
    
    private Integer resourceDisk;
    
    private String networkConfig;
    
    private Boolean isPublic;
    
    private Integer timeoutSeconds;
}
