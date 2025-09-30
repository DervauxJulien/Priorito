package com.example.todoapp.service;

import com.example.todoapp.model.Task;
import com.example.todoapp.model.User;
import com.example.todoapp.payload.TaskResponse;
import com.example.todoapp.payload.UserResponse;
import com.example.todoapp.repository.TaskRepository;
import com.example.todoapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public AdminService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    public ResponseEntity<?> deleteUser(Long id, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur actuel introuvable"));

        if (currentUser.getId().equals(id)) {
            return ResponseEntity.badRequest().body("Un administrateur ne peut pas se supprimer lui-même !");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("Utilisateur supprimé !");
    }

    public String deleteAnyTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        taskRepository.delete(task);
        return "Tâche supprimée !";
    }

    public TaskResponse createTaskForUser(Task taskRequest) {
        User user = userRepository.findById(taskRequest.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        taskRequest.setUser(user);
        taskRequest.setCompleted(false);

        Task savedTask = taskRepository.save(taskRequest);
        return new TaskResponse(savedTask);
    }
}

