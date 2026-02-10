package com.aws.tasktracker.integration;

import com.aws.tasktracker.model.Task;
import com.aws.tasktracker.repository.DynamoDbTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DynamoDbIntegrationTest {

    @Autowired
    private DynamoDbTaskRepository dynamoRepo;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        // DynamoDB repo requires non-null id; generate a unique one for tests
        task.setId(System.currentTimeMillis());
        task.setTitle("Dynamo Test");
        task.setDescription("Verify CRUD against DynamoDB");
        task.setAttachmentUrl("s3://placeholder/key.txt");
        task.setCompleted(false);
    }

    @Test
    void save_and_findById() {
        dynamoRepo.save(task);
        Optional<Task> found = dynamoRepo.findById(task.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Dynamo Test");
        assertThat(found.get().isCompleted()).isFalse();
    }

    @Test
    void deleteById() {
        dynamoRepo.save(task);
        dynamoRepo.deleteById(task.getId());
        Optional<Task> found = dynamoRepo.findById(task.getId());
        assertThat(found).isNotPresent();
    }
}
