package com.agi.learning.feedback.entity;

import com.agi.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String sourceType;
    
    @Column(nullable = false)
    private String sourceId;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedbackType type;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private Integer rating;
    
    @Column(columnDefinition = "JSON")
    private String metadata;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Builder
    public Feedback(User user, String sourceType, String sourceId, FeedbackType type, 
                   String content, Integer rating, String metadata) {
        this.user = user;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.type = type;
        this.content = content;
        this.rating = rating;
        this.metadata = metadata;
        this.createdAt = LocalDateTime.now();
    }
    
    public enum FeedbackType {
        THUMBS_UP, THUMBS_DOWN, RATING, TEXT, CORRECTION
    }
}
