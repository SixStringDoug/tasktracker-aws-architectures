package com.aws.tasktracker.integration;

import com.aws.tasktracker.TasktrackerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.File;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TasktrackerApplication.class)
public class S3AndRdsIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private S3Client s3Client;

    private final String bucketName = "aws-saa-tasktracker-bucket-dp"; // match your actual bucket
    private final String testKey = "integration-test.txt";

    @Test
    public void testJdbcTemplateQuery() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertNotNull(result);
        assertEquals(1, result);
    }

    @Test
    void testRdsConnectionAndQuery() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS integration_test (id SERIAL PRIMARY KEY, name VARCHAR(50));");
            stmt.execute("INSERT INTO integration_test (name) VALUES ('TestName');");

            ResultSet rs = stmt.executeQuery("SELECT name FROM integration_test LIMIT 1;");
            assertTrue(rs.next(), "No rows returned from RDS");
            assertEquals("TestName", rs.getString("name"));
        }
    }

    @Test
    void testS3PutAndGetObject() throws Exception {
        // Create a temp file
        File tempPutFile = File.createTempFile("integration-test", ".txt");
        String tempString = "Hello from integration test";
        java.nio.file.Files.write(tempPutFile.toPath(), tempString.getBytes());

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(testKey)
                .build();

        // Upload file to S3
        s3Client.putObject(putRequest, RequestBody.fromString(tempString));

        // Delete a temp file if it exists
        Path tempGetFile = Paths.get(System.getProperty("java.io.tmpdir"), "downloaded-test.txt");
        Files.deleteIfExists(tempGetFile);

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(testKey)
                .build();

        // Download file from S3
        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getRequest);

        // Read content from stream correctly:
        String retrievedContent = new BufferedReader(new InputStreamReader(s3Object))
                .lines().collect(Collectors.joining("\n"));

        assertEquals(tempString, retrievedContent);
    }
}
