package com.agi.conversation.repository;

import com.agi.conversation.entity.Conversation;
import com.agi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
    List<Conversation> findByUser(User user);
    
    @Query("SELECT c FROM Conversation c WHERE c.user.id = :userId")
    List<Conversation> findByUserId(Long userId);
    
    @Query("SELECT c FROM Conversation c WHERE c.user.id = :userId ORDER BY c.lastMessageAt DESC")
    List<Conversation> findByUserIdOrderByLastMessageAtDesc(Long userId);
    
    @Query("SELECT c FROM Conversation c WHERE c.createdAt >= :startDate AND c.createdAt <= :endDate")
    List<Conversation> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
