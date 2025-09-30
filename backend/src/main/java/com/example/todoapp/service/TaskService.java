package com.example.todoapp.service;

import com.example.todoapp.model.Task;
import com.example.todoapp.model.User;
import com.example.todoapp.payload.TaskResponse;
import com.example.todoapp.repository.TaskRepository;
import com.example.todoapp.repository.UserRepository;
import com.example.todoapp.security.UserDetailsImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<TaskResponse> getTasks(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return taskRepository.findByUser(user)
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    public TaskResponse createTask(UserDetailsImpl userDetails, Task taskRequest) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setPriority(taskRequest.getPriority());
        task.setCompleted(false);
        task.setUser(user);

        return new TaskResponse(taskRepository.save(task));
    }

    public TaskResponse createTaskForUser(Task taskRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        taskRequest.setUser(user);
        return new TaskResponse(taskRepository.save(taskRequest));
    }

    public TaskResponse updateTask(UserDetailsImpl userDetails, Long id, Task taskDetails) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setCompleted(taskDetails.isCompleted());
        task.setPriority(taskDetails.getPriority());
        return new TaskResponse(taskRepository.save(task));
    }

    public String deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        taskRepository.delete(task);
        return "Tâche supprimée !";
    }
}

