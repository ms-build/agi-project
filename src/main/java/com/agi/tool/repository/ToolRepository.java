package com.agi.tool.repository;

import com.agi.tool.entity.Tool;
import com.agi.tool.enums.ToolStatus;
import com.agi.tool.enums.ToolType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<Tool, String> {
    List<Tool> findByStatus(ToolStatus status);
    
    List<Tool> findByType(ToolType type);
    
    Optional<Tool> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT t FROM Tool t WHERE t.status = :status AND t.type = :type")
    List<Tool> findByStatusAndType(ToolStatus status, ToolType type);
}
