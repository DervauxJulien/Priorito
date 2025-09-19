package com.example.todoapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String title;
    private String description;
    private boolean completed = false;
    private int priority = 2; // 1=haute, 2=moyenne, 3=basse

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public Long getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
