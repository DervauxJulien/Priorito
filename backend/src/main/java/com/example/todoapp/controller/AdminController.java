package com.example.todoapp.controller;

import com.example.todoapp.model.Task;
import com.example.todoapp.payload.TaskResponse;
import com.example.todoapp.payload.UserResponse;
import com.example.todoapp.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        return adminService.deleteUser(id, authentication);
    }

    @DeleteMapping("/tasks/{id}")
    public String deleteAnyTask(@PathVariable Long id) {
        return adminService.deleteAnyTask(id);
    }

    @PostMapping("/tasks")
    public TaskResponse createTaskForUser(@RequestBody Task taskRequest) {
        return adminService.createTaskForUser(taskRequest);
    }
}
