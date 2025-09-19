package com.example.todoapp.payload;

import com.example.todoapp.model.Task;
import java.time.LocalDateTime;

/**
 * DTO utilisé pour renvoyer les informations d'une tâche vers le frontend.
 * Contient toutes les informations nécessaires pour afficher ou gérer une tâche.
 */
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private int priority;
    private LocalDateTime createdAt;
    private Long userId; // ID de l'utilisateur propriétaire de la tâche

    /**
     * Construit un TaskResponse à partir d'une entité Task.
     * Permet de ne pas exposer directement l'entité dans les réponses HTTP.
     */
    public TaskResponse(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.completed = task.isCompleted();
        this.priority = task.getPriority();
        this.createdAt = task.getCreatedAt();
        this.userId = task.getUser() != null ? task.getUser().getId() : null;
    }

    // Getters et setters standards
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
