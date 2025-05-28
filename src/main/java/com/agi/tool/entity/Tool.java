package com.agi.tool.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tool")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @Column(nullable = false, length = 200)
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String documentation;
    
    @Column(nullable = false)
    private boolean isActive;
    
    @Column(columnDefinition = "JSON")
    private String schema;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "tool", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ToolParameter> parameters = new ArrayList<>();
    
    @Builder
    public Tool(String name, String description, String documentation, String schema) {
        this.name = name;
        this.description = description;
        this.documentation = documentation;
        this.schema = schema;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    
    public void update(String description, String documentation, String schema) {
        this.description = description;
        this.documentation = documentation;
        this.schema = schema;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addParameter(ToolParameter parameter) {
        this.parameters.add(parameter);
        this.updatedAt = LocalDateTime.now();
    }
}
