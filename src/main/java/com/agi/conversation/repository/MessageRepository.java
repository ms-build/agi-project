package com.agi.conversation.repository;

import com.agi.conversation.entity.Message;
import com.agi.conversation.entity.Conversation;
import com.agi.conversation.enums.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findByConversation(Conversation conversation);
    
    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);
    
    List<Message> findByType(MessageType type);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt ASC")
    List<Message> findByConversationIdOrderByCreatedAtAsc(String conversationId);
    
    @Query("SELECT m FROM Message m WHERE m.createdAt >= :startDate AND m.createdAt <= :endDate")
    List<Message> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
