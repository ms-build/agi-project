package com.agi.tool.entity;

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

import com.agi.tool.enums.ToolStatus;
import com.agi.user.entity.User;

@Entity
@Table(name = "tool_execution")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToolExecution {
    
    @Id
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "tool_id", nullable = false)
    private Tool tool;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime executedAt;
    
    private LocalDateTime completedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ToolStatus status;
    
    @Column(columnDefinition = "JSON")
    private String parameters;
    
    @Column(columnDefinition = "JSON")
    private String result;
    
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    private Long executionTimeMs;
    
    private String conversationId;
    
    private String messageId;
}
