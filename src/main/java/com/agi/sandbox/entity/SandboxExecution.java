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
@Table(name = "sandbox_execution")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SandboxExecution {
    
    @Id
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "sandbox_id", nullable = false)
    private Sandbox sandbox;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    @Column(nullable = false)
    private String status;
    
    private String command;
    
    @Column(columnDefinition = "TEXT")
    private String output;
    
    @Column(columnDefinition = "TEXT")
    private String errorOutput;
    
    private Integer exitCode;
    
    private String resourceUsage;
}
