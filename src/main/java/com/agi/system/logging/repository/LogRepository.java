package com.agi.system.logging.repository;

import com.agi.system.logging.entity.Log;
import com.agi.system.logging.enums.LogLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, String> {
    
    List<Log> findByLevel(LogLevel level);
    
    List<Log> findBySource(String source);
    
    List<Log> findByUserId(String userId);
    
    List<Log> findBySessionId(String sessionId);
    
    List<Log> findByRequestId(String requestId);
    
    @Query("SELECT l FROM Log l WHERE l.timestamp >= :startDate AND l.timestamp <= :endDate")
    List<Log> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT l FROM Log l WHERE l.message LIKE %:keyword%")
    List<Log> findByMessageContaining(String keyword);
    
    @Query("SELECT l FROM Log l WHERE l.stackTrace LIKE %:keyword%")
    List<Log> findByStackTraceContaining(String keyword);
}
