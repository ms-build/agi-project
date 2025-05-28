package com.agi.tool.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tool_parameter")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ToolParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id", nullable = false)
    private Tool tool;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 50)
    private String type;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private boolean required;
    
    @Column(name = "default_value")
    private String defaultValue;
    
    @Builder
    public ToolParameter(Tool tool, String name, String type, String description, boolean required, String defaultValue) {
        this.tool = tool;
        this.name = name;
        this.type = type;
        this.description = description;
        this.required = required;
        this.defaultValue = defaultValue;
    }
    
    public void updateDescription(String description) {
        this.description = description;
    }
    
    public void updateDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
