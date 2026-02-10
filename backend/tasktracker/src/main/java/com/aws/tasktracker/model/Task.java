package com.aws.tasktracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Task {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    private String attachmentUrl;

    private boolean completed = false;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAttachmentUrl() {return attachmentUrl;}
    public void setAttachmentUrl(String attachmentUrl) {this.attachmentUrl = attachmentUrl;}

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
