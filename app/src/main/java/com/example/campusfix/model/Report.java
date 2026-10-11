package com.example.campusfix.model;

import java.io.Serializable;

public class Report implements Serializable {
    private String id;
    private String title;
    private String description;
    private String category;
    private String location;
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL
    private String status;   // OPEN, IN_PROGRESS, RESOLVED, REJECTED
    private String imagePath;
    private long createdAt;
    private long updatedAt;
    private boolean isHazard;
    private String contactEmail;
    private boolean isDemo;

    public Report() {
    }

    public Report(String id, String title, String description, String category, String location,
                  String priority, String status, String imagePath, long createdAt,
                  long updatedAt, boolean isHazard, String contactEmail) {
        this(id, title, description, category, location, priority, status, imagePath, createdAt, updatedAt, isHazard, contactEmail, false);
    }

    public Report(String id, String title, String description, String category, String location,
                  String priority, String status, String imagePath, long createdAt,
                  long updatedAt, boolean isHazard, String contactEmail, boolean isDemo) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.priority = priority;
        this.status = status;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isHazard = isHazard;
        this.contactEmail = contactEmail;
        this.isDemo = isDemo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isHazard() {
        return isHazard;
    }

    public void setHazard(boolean hazard) {
        isHazard = hazard;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public boolean isDemo() {
        return isDemo;
    }

    public void setDemo(boolean demo) {
        isDemo = demo;
    }
}
