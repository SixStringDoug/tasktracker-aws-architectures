package com.aws.tasktracker.service;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class AttachmentService {
    private static final Logger log = LoggerFactory.getLogger(AttachmentService.class);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public AttachmentService(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    /**
     * Uploads MultipartFile to S3 using configured bucket.
     * Returns the S3 object key (e.g. "attachments/{uuid}-{filename}").
     */
    public String uploadAttachment(MultipartFile file) throws IOException {
        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Upload to S3
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

        // Generate presigned URL for GET
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                        .getObjectRequest(getRequest)
                        .signatureDuration(Duration.ofMinutes(10)) // URL expires in 10 min
                        .build()
        );

        return presignedRequest.url().toString();
    }

    /**
     * Downloads an object from S3 and returns its bytes.
     * Declares IOException so callers can handle it.
     */
    public byte[] downloadAttachment(String key) throws IOException {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            ResponseBytes<?> resp = s3Client.getObject(getReq, ResponseTransformer.toBytes());
            return resp.asByteArray();
        } catch (SdkException e) {
            log.error("S3 download failed for key {}", key, e);
            throw new IOException("S3 download failed for key: " + key, e);
        }
    }

    /**
     * Deletes an S3 object. Caller is responsible for updating any DB references.
     * Returns true if operation completed without throwing (note: S3 delete is idempotent).
     */
    public boolean deleteAttachment(String key) {
        DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            s3Client.deleteObject(delReq);
            log.info("Deleted object s3://{}/{}", bucketName, key);
            return true;
        } catch (SdkException e) {
            log.error("Failed to delete S3 object {}", key, e);
            return false;
        }
    }

    private String sanitize(String name) {
        return name == null ? "file" : name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
