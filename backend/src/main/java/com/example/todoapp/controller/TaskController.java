package com.example.todoapp.controller;

import com.example.todoapp.model.Task;
import com.example.todoapp.payload.TaskResponse;
import com.example.todoapp.security.UserDetailsImpl;
import com.example.todoapp.service.TaskService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> getTasks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return taskService.getTasks(userDetails);
    }

    @PostMapping
    public TaskResponse createTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @RequestBody Task taskRequest) {
        return taskService.createTask(userDetails, taskRequest);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse createTaskForUser(@RequestBody Task taskRequest, @RequestParam Long userId) {
        return taskService.createTaskForUser(taskRequest, userId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@taskSecurity.canAccessTask(#id, principal)")
    public TaskResponse updateTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @PathVariable Long id,
                                   @RequestBody Task taskDetails) {
        return taskService.updateTask(userDetails, id, taskDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@taskSecurity.canAccessTask(#id, principal)")
    public String deleteTask(@PathVariable Long id) {
        return taskService.deleteTask(id);
    }
}
