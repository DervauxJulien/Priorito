package com.example.todoapp.repository;

import com.example.todoapp.model.Task;
import com.example.todoapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository pour l'entité Task.
 * Permet de gérer la persistance des tâches en base de données
 * et de récupérer des tâches liées à un utilisateur spécifique.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Récupère toutes les tâches associées à un utilisateur donné.
     * @param user l'utilisateur propriétaire des tâches
     * @return liste de tâches
     */
    List<Task> findByUser(User user);

    /**
     * Récupère toutes les tâches associées à un utilisateur donné,
     * triées par identifiant croissant.
     * @param user l'utilisateur propriétaire des tâches
     * @return liste de tâches triées
     */
    List<Task> findByUserOrderByIdAsc(User user);
}
