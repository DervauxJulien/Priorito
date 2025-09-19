package com.example.todoapp.payload;

import com.example.todoapp.model.User;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO pour exposer les informations d'un utilisateur via l'API.
 *
 * Contient l'id, le username, le rôle et la liste des tâches associées.
 */
public class UserResponse {
    private Long id;
    private String username;
    private String role;
    private List<TaskResponse> tasks;

    /**
     * Construit un UserResponse à partir d'un User.
     *
     * @param user l'utilisateur source
     */
    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole().name();
        // Transforme les tâches en TaskResponse, ou null si pas de tâches
        this.tasks = user.getTasks() != null
                ? user.getTasks()
                .stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList())
                : null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    /**
     * Retourne la liste des tâches de l'utilisateur.
     * Peut être null si l'utilisateur n'a pas encore de tâches.
     *
     * @return liste des TaskResponse ou null
     */
    public List<TaskResponse> getTasks() { return tasks; }

    public void setTasks(List<TaskResponse> tasks) { this.tasks = tasks; }
}
