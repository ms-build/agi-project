package com.agi.knowledge.repository;

import com.agi.knowledge.entity.KnowledgeTag;
import com.agi.knowledge.entity.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgeTagRepository extends JpaRepository<KnowledgeTag, Long> {
    List<KnowledgeTag> findByKnowledge(Knowledge knowledge);
    
    Optional<KnowledgeTag> findByName(String name);
    
    @Query("SELECT kt FROM KnowledgeTag kt WHERE kt.knowledge.id = :knowledgeId")
    List<KnowledgeTag> findByKnowledgeId(String knowledgeId);
    
    @Query("SELECT kt FROM KnowledgeTag kt WHERE kt.name LIKE %:keyword%")
    List<KnowledgeTag> findByNameContaining(String keyword);
    
    boolean existsByName(String name);
}
