package com.aws.tasktracker.repository;

import com.aws.tasktracker.model.Task;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Repository
public class DynamoDbTaskRepository {

    private final DynamoDbClient dynamoDbClient;
    private static final String TABLE_NAME = "Tasks";

    public DynamoDbTaskRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void save(Task task) {
        if (task.getId() == null) {
            // In RDS id is auto-generated; for DynamoDB you can enforce non-null or generate one
            throw new IllegalArgumentException("Task.id must be set before saving to DynamoDB");
        }

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().n(task.getId().toString()).build());
        item.put("title", AttributeValue.builder().s(nvl(task.getTitle())).build());
        if (task.getDescription() != null)
            item.put("description", AttributeValue.builder().s(task.getDescription()).build());
        if (task.getAttachmentUrl() != null)
            item.put("attachmentUrl", AttributeValue.builder().s(task.getAttachmentUrl()).build());
        item.put("completed", AttributeValue.builder().bool(task.isCompleted()).build());

        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build());
    }

    public Optional<Task> findById(Long id) {
        Map<String, AttributeValue> key = Map.of("id", AttributeValue.builder().n(id.toString()).build());

        GetItemResponse res = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .build());

        if (!res.hasItem() || res.item().isEmpty()) return Optional.empty();
        return Optional.of(mapToTask(res.item()));
    }

    public void deleteById(Long id) {
        Map<String, AttributeValue> key = Map.of("id", AttributeValue.builder().n(id.toString()).build());
        dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .build());
    }

    // Optional: list all (scan). Use cautiously in prod.
    public List<Task> findAll() {
        ScanResponse scan = dynamoDbClient.scan(ScanRequest.builder().tableName(TABLE_NAME).build());
        List<Task> out = new ArrayList<>();
        if (scan.hasItems()) {
            for (Map<String, AttributeValue> it : scan.items()) out.add(mapToTask(it));
        }
        return out;
    }

    private Task mapToTask(Map<String, AttributeValue> item) {
        Task t = new Task();
        if (item.containsKey("id")) t.setId(Long.valueOf(item.get("id").n()));
        if (item.containsKey("title")) t.setTitle(item.get("title").s());
        if (item.containsKey("description")) t.setDescription(item.get("description").s());
        if (item.containsKey("attachmentUrl")) t.setAttachmentUrl(item.get("attachmentUrl").s());
        if (item.containsKey("completed")) t.setCompleted(item.get("completed").bool());
        return t;
    }

    private static String nvl(String s) { return (s == null) ? "" : s; }
}
