package com.aws.tasktracker.service;

import com.aws.tasktracker.model.Task;
import com.aws.tasktracker.repository.TaskRepository;
import com.aws.tasktracker.repository.DynamoDbTaskRepository;
import com.aws.tasktracker.config.EnvironmentSelector;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;                 // JPA → RDS
    private final DynamoDbTaskRepository dynamoDbTaskRepository; // DynamoDB
    private final EnvironmentSelector environmentSelector;

    public TaskService(TaskRepository taskRepository,
                       DynamoDbTaskRepository dynamoDbTaskRepository,
                       EnvironmentSelector environmentSelector) {
        this.taskRepository = taskRepository;
        this.dynamoDbTaskRepository = dynamoDbTaskRepository;
        this.environmentSelector = environmentSelector;
    }

    public List<Task> findAll() {
        if (environmentSelector.useDynamoDb()) {
            return dynamoDbTaskRepository.findAll();
        }
        return taskRepository.findAll();
    }

    /**
     * Updates the attachment URL for a given task (RDS/JPA path).
     * Keeps original behavior unchanged.
     */
    public void updateTaskAttachmentUrl(Long taskId, String fileUrl) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setAttachmentUrl(fileUrl);
            taskRepository.save(task);
        } else {
            throw new RuntimeException("Task with ID " + taskId + " not found");
        }
    }

    public Task createTask(Task task) {
        if (environmentSelector.useDynamoDb()) {
            // Dynamo path
            if (task.getId() == null) {
                // DynamoDbTaskRepository expects non-null id (since DynamoDB doesn’t auto-generate)
                task.setId(System.currentTimeMillis());
            }
            dynamoDbTaskRepository.save(task);
            return task;
        }
        // RDS path (original)
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long taskId) {
        if (environmentSelector.useDynamoDb()) {
            return dynamoDbTaskRepository.findById(taskId);
        }
        return taskRepository.findById(taskId);
    }

    public void deleteTask(Long taskId) {
        if (environmentSelector.useDynamoDb()) {
            dynamoDbTaskRepository.deleteById(taskId);
        } else {
            taskRepository.deleteById(taskId);
        }
    }
}
