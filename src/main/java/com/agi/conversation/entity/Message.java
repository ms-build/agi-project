package com.agi.conversation.entity;

import com.agi.conversation.enums.MessageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {
    @Id
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType role;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(columnDefinition = "BLOB")
    private byte[] embedding;
    
    @Column(columnDefinition = "JSON")
    private Map<String, Object> metadata = new HashMap<>();
    
    @Builder
    public Message(String id, Conversation conversation, MessageType role, String content, Map<String, Object> metadata) {
        this.id = id;
        this.conversation = conversation;
        this.role = role;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        if (metadata != null) {
            this.metadata = metadata;
        }
    }
    
    public void updateContent(String content) {
        this.content = content;
    }
    
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    public void setEmbedding(byte[] embedding) {
        this.embedding = embedding;
    }
}
