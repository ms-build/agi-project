package com.agi.plan.entity;

import com.agi.plan.enums.PlanStatus;
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
@Table(name = "plan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlanStatus status;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime completedAt;
    
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanStep> steps = new ArrayList<>();
    
    @Column(columnDefinition = "JSON")
    private String metadata;
    
    @Builder
    public Plan(User user, String title, String description) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.status = PlanStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    
    public void start() {
        this.status = PlanStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void complete() {
        this.status = PlanStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = this.completedAt;
    }
    
    public void fail() {
        this.status = PlanStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addStep(PlanStep step) {
        this.steps.add(step);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateMetadata(String metadata) {
        this.metadata = metadata;
        this.updatedAt = LocalDateTime.now();
    }
}
