package com.agi.ai.vision.repository;

import com.agi.ai.vision.entity.ImageAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ImageAnalysisRepository extends JpaRepository<ImageAnalysis, String> {
    
    List<ImageAnalysis> findByImageUrlContaining(String imageUrl);
    
    @Query("SELECT ia FROM ImageAnalysis ia WHERE ia.analyzedAt >= :startDate AND ia.analyzedAt <= :endDate")
    List<ImageAnalysis> findByAnalyzedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT ia FROM ImageAnalysis ia WHERE ia.isSuccessful = :isSuccessful")
    List<ImageAnalysis> findByIsSuccessful(Boolean isSuccessful);
    
    @Query("SELECT ia FROM ImageAnalysis ia WHERE ia.dominantColor = :color")
    List<ImageAnalysis> findByDominantColor(String color);
}
