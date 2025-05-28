package com.agi.sandbox.entity;

import com.agi.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sandbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sandbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String sandboxId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SandboxStatus status;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime terminatedAt;
    
    @Column(columnDefinition = "JSON")
    private String configuration;
    
    @Column(columnDefinition = "JSON")
    private String securityPolicy;
    
    @Column(columnDefinition = "JSON")
    private String resourceLimits;
    
    @Builder
    public Sandbox(String sandboxId, User user, String configuration, 
                  String securityPolicy, String resourceLimits) {
        this.sandboxId = sandboxId;
        this.user = user;
        this.status = SandboxStatus.CREATED;
        this.configuration = configuration;
        this.securityPolicy = securityPolicy;
        this.resourceLimits = resourceLimits;
        this.createdAt = LocalDateTime.now();
    }
    
    public void start() {
        this.status = SandboxStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }
    
    public void pause() {
        this.status = SandboxStatus.PAUSED;
    }
    
    public void resume() {
        this.status = SandboxStatus.RUNNING;
    }
    
    public void terminate() {
        this.status = SandboxStatus.TERMINATED;
        this.terminatedAt = LocalDateTime.now();
    }
    
    public enum SandboxStatus {
        CREATED, RUNNING, PAUSED, TERMINATED, ERROR
    }
}
