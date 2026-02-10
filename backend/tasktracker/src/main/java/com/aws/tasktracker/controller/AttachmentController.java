package com.aws.tasktracker.controller;

import com.aws.tasktracker.service.AttachmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadAttachment(@RequestParam("file") MultipartFile file) {
        try {
            // This calls the convenience method (added below) that handles defaults.
            String s3KeyOrUrl = attachmentService.uploadAttachment(file);
            return ResponseEntity.ok(s3KeyOrUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Attachment upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/download/{key}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable String key) {
        try {
            byte[] data = attachmentService.downloadAttachment(key);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/delete/{key}")
    public ResponseEntity<String> deleteAttachment(@PathVariable String key) {
        try {
            attachmentService.deleteAttachment(key);
            return ResponseEntity.ok("File deleted successfully: " + key);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting file: " + key + " : " + e.getMessage());
        }
    }

}
