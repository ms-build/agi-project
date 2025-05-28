package com.agi.sandbox.repository;

import com.agi.sandbox.entity.SandboxTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SandboxTemplateRepository extends JpaRepository<SandboxTemplate, String> {
    Optional<SandboxTemplate> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT st FROM SandboxTemplate st WHERE st.isActive = true")
    List<SandboxTemplate> findAllActive();
    
    @Query("SELECT st FROM SandboxTemplate st WHERE st.name LIKE %:keyword%")
    List<SandboxTemplate> findByNameContaining(String keyword);
}
