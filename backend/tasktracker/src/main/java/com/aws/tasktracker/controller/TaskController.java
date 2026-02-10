package com.aws.tasktracker.controller;

import com.aws.tasktracker.model.Task;
import com.aws.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService; // use service to switch RDS/Dynamo

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        // For DynamoDB path, implement findAll() in repo if needed; otherwise RDS works now.
        return taskService.findAll(); // if not implemented for Dynamo yet, you can temporarily return List.of()
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task saved = taskService.createTask(task);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task taskDetails) {
        Optional<Task> current = taskService.getTaskById(id);
        if (current.isEmpty()) return ResponseEntity.notFound().build();

        Task t = current.get();
        t.setTitle(taskDetails.getTitle());
        t.setDescription(taskDetails.getDescription());
        t.setCompleted(taskDetails.isCompleted());
        t.setAttachmentUrl(taskDetails.getAttachmentUrl());

        return ResponseEntity.ok(taskService.createTask(t));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Optional<Task> current = taskService.getTaskById(id);
        if (current.isEmpty()) return ResponseEntity.notFound().build();
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
