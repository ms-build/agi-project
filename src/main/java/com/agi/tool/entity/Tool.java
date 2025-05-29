package com.agi.tool.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import com.agi.tool.enums.ToolType;
import com.agi.tool.enums.ToolStatus;

@Entity
@Table(name = "tool")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tool {
    
    @Id
    private String id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ToolType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ToolStatus status;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String version;
    
    @Column(columnDefinition = "TEXT")
    private String schema;
    
    private String endpoint;
    
    private Boolean isPublic;
    
    @OneToMany(mappedBy = "tool")
    private List<ToolParameter> parameters = new ArrayList<>();
    
    @OneToMany(mappedBy = "tool")
    private List<ToolExecution> executions = new ArrayList<>();
}
