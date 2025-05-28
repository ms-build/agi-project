package com.agi.ai.nlp.repository;

import com.agi.ai.nlp.entity.SentimentAnalysis;
import com.agi.ai.nlp.enums.SentimentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SentimentAnalysisRepository extends JpaRepository<SentimentAnalysis, String> {
    
    List<SentimentAnalysis> findBySentiment(SentimentType sentiment);
    
    @Query("SELECT sa FROM SentimentAnalysis sa WHERE sa.analyzedAt >= :startDate AND sa.analyzedAt <= :endDate")
    List<SentimentAnalysis> findByAnalyzedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT sa FROM SentimentAnalysis sa WHERE sa.text LIKE %:keyword%")
    List<SentimentAnalysis> findByTextContaining(String keyword);
    
    @Query("SELECT sa FROM SentimentAnalysis sa WHERE sa.language = :language")
    List<SentimentAnalysis> findByLanguage(String language);
}
