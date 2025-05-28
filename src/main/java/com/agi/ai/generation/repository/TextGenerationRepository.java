package com.agi.ai.generation.repository;

import com.agi.ai.generation.entity.TextGeneration;
import com.agi.ai.generation.enums.GenerationModelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TextGenerationRepository extends JpaRepository<TextGeneration, String> {
    
    List<TextGeneration> findByModelType(GenerationModelType modelType);
    
    @Query("SELECT tg FROM TextGeneration tg WHERE tg.createdAt >= :startDate AND tg.createdAt <= :endDate")
    List<TextGeneration> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT tg FROM TextGeneration tg WHERE tg.tokensUsed >= :minTokens")
    List<TextGeneration> findByTokensUsedGreaterThanEqual(Integer minTokens);
    
    @Query("SELECT tg FROM TextGeneration tg WHERE tg.prompt LIKE %:keyword% OR tg.generatedText LIKE %:keyword%")
    List<TextGeneration> findByPromptOrGeneratedTextContaining(String keyword);
    
    List<TextGeneration> findByIsSuccessful(Boolean isSuccessful);
}
