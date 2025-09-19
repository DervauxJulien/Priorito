package com.example.todoapp.controller;

import com.example.todoapp.model.Task;
import com.example.todoapp.model.User;
import com.example.todoapp.payload.TaskResponse;
import com.example.todoapp.repository.TaskRepository;
import com.example.todoapp.repository.UserRepository;
import com.example.todoapp.security.TaskSecurity;
import com.example.todoapp.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskSecurity taskSecurity;

    /**
     * Récupère toutes les tâches de l'utilisateur connecté.
     *
     * @param userDetails info de l'utilisateur connecté
     * @return liste des tâches sous forme de TaskResponse
     */
    @GetMapping
    public List<TaskResponse> getTasks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return taskRepository.findByUser(user)
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    /**
     * Crée une nouvelle tâche pour l'utilisateur connecté.
     *
     * @param userDetails utilisateur connecté
     * @param taskRequest données de la tâche à créer
     * @return TaskResponse représentant la tâche créée
     */
    @PostMapping
    public TaskResponse createTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @RequestBody Task taskRequest) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setPriority(taskRequest.getPriority());
        task.setCompleted(false); // nouvelle tâche non complétée
        task.setUser(user);

        return new TaskResponse(taskRepository.save(task));
    }

    /**
     * Crée une tâche pour un autre utilisateur (seulement admin).
     *
     * @param taskRequest données de la tâche
     * @param userId id de l'utilisateur cible
     * @return TaskResponse de la tâche créée
     */
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // uniquement admin
    public TaskResponse createTaskForUser(@RequestBody Task taskRequest, @RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        taskRequest.setUser(user);
        return new TaskResponse(taskRepository.save(taskRequest));
    }

    /**
     * Met à jour une tâche existante.
     *
     * @param userDetails utilisateur connecté
     * @param id id de la tâche à mettre à jour
     * @param taskDetails nouvelles données de la tâche
     * @return TaskResponse de la tâche mise à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("@taskSecurity.canAccessTask(#id, principal)") // vérifie droits
    public TaskResponse updateTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @PathVariable Long id,
                                   @RequestBody Task taskDetails) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setCompleted(taskDetails.isCompleted());
        task.setPriority(taskDetails.getPriority());
        return new TaskResponse(taskRepository.save(task));
    }

    /**
     * Supprime une tâche.
     *
     * @param id id de la tâche à supprimer
     * @return message de confirmation
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@taskSecurity.canAccessTask(#id, principal)") // vérifie droits
    public String deleteTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        taskRepository.delete(task);
        return "Tâche supprimée !";
    }
}


