package com.agi.conversation.entity;

import com.agi.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conversation {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 200)
    private String title;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @Column(columnDefinition = "JSON")
    private String metadata;

    @Builder
    public Conversation(String id, User user, String title) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void updateTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateSummary(String summary) {
        this.summary = summary;
        this.updatedAt = LocalDateTime.now();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateMetadata(String metadata) {
        this.metadata = metadata;
        this.updatedAt = LocalDateTime.now();
    }
}
