package com.example.todoapp.controller;

import com.example.todoapp.model.Task;
import com.example.todoapp.model.User;
import com.example.todoapp.payload.TaskResponse;
import com.example.todoapp.payload.UserResponse;
import com.example.todoapp.repository.TaskRepository;
import com.example.todoapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Tous les endpoints ici sont réservés aux admins
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Récupère la liste complète des utilisateurs avec leurs informations essentielles.
     *
     * @return liste des utilisateurs transformée en UserResponse
     */
    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new) // Conversion vers DTO pour ne pas exposer toutes les infos sensibles
                .collect(Collectors.toList());
    }

    /**
     * Supprime un utilisateur par son ID.
     * Empêche l'admin courant de se supprimer lui-même.
     *
     * @param id ID de l'utilisateur à supprimer
     * @param authentication info sur l'utilisateur courant
     * @return réponse HTTP avec message
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur actuel introuvable"));

        // Sécurité : un admin ne peut pas se supprimer lui-même
        if (currentUser.getId().equals(id)) {
            return ResponseEntity
                    .badRequest()
                    .body("Un administrateur ne peut pas se supprimer lui-même !");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("Utilisateur supprimé !");
    }

    /**
     * Supprime n'importe quelle tâche par son ID.
     *
     * @param id ID de la tâche à supprimer
     * @return message de confirmation
     */
    @DeleteMapping("/tasks/{id}")
    public String deleteAnyTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        taskRepository.delete(task);
        return "Tâche supprimée !";
    }

    /**
     * Crée une tâche pour un utilisateur spécifique.
     * La tâche sera initialisée avec completed = false.
     *
     * @param taskRequest tâche à créer, avec l'utilisateur assigné
     * @return la tâche sauvegardée transformée en TaskResponse
     */
    @PostMapping("/tasks")
    public TaskResponse createTaskForUser(@RequestBody Task taskRequest) {
        User user = userRepository.findById(taskRequest.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        taskRequest.setUser(user);
        taskRequest.setCompleted(false);

        Task savedTask = taskRepository.save(taskRequest);
        return new TaskResponse(savedTask);
    }
}
