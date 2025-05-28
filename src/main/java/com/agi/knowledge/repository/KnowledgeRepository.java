package com.agi.knowledge.repository;

import com.agi.knowledge.entity.Knowledge;
import com.agi.knowledge.enums.KnowledgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KnowledgeRepository extends JpaRepository<Knowledge, String> {
    List<Knowledge> findByType(KnowledgeType type);
    
    @Query("SELECT k FROM Knowledge k WHERE k.content LIKE %:keyword%")
    List<Knowledge> findByContentContaining(String keyword);
    
    @Query("SELECT k FROM Knowledge k JOIN k.tags t WHERE t.name = :tagName")
    List<Knowledge> findByTagName(String tagName);
    
    @Query("SELECT k FROM Knowledge k WHERE k.createdAt >= :startDate AND k.createdAt <= :endDate")
    List<Knowledge> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT k FROM Knowledge k WHERE k.type = :type AND k.content LIKE %:keyword%")
    List<Knowledge> findByTypeAndContentContaining(KnowledgeType type, String keyword);
}
