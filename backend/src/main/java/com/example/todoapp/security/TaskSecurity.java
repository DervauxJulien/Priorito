package com.example.todoapp.security;

import com.example.todoapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Composant Spring qui gère la sécurité au niveau des tâches.
 * Permet de vérifier si un utilisateur peut accéder ou modifier une tâche spécifique.
 */
@Component
public class TaskSecurity {

    @Autowired
    private TaskRepository taskRepository; // accès aux tâches en DB

    /**
     * Vérifie si l'utilisateur peut accéder à la tâche avec l'id donné.
     *
     * Règles :
     *   1. Un utilisateur avec le rôle ADMIN a toujours accès.
     *   2. Sinon, l'utilisateur ne peut accéder qu'aux tâches qu'il possède.
     *
     * @param taskId     l'identifiant de la tâche
     * @param userDetails les informations de l'utilisateur courant
     * @return true si l'utilisateur peut accéder à la tâche, false sinon
     */
    public boolean canAccessTask(Long taskId, UserDetailsImpl userDetails) {
        return userDetails
                .getAuthorities()
                .stream()
                // si l'utilisateur est ADMIN → accès autorisé
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))
                ||
                // sinon, vérifier que la tâche appartient bien à l'utilisateur
                taskRepository.findById(taskId)
                        .map(task -> task.getUser().getId().equals(userDetails.getId()))
                        .orElse(false); // si la tâche n'existe pas → accès refusé
    }
}
