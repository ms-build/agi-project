package com.agi.tool.repository;

import com.agi.tool.entity.ToolParameter;
import com.agi.tool.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolParameterRepository extends JpaRepository<ToolParameter, Long> {
    List<ToolParameter> findByTool(Tool tool);
    
    List<ToolParameter> findByToolAndRequired(Tool tool, boolean required);
    
    @Query("SELECT tp FROM ToolParameter tp WHERE tp.tool.id = :toolId")
    List<ToolParameter> findByToolId(String toolId);
    
    @Query("SELECT tp FROM ToolParameter tp WHERE tp.tool.id = :toolId AND tp.name = :name")
    ToolParameter findByToolIdAndName(String toolId, String name);
}
