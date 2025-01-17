package com.example.merainstitue.utils;

public class NotificationModel {
    private String id;
    private String userId;
    private String courseId;
    private String title;
    private String message;
    private long timestamp;
    private boolean isRead;
    private String type;

    public NotificationModel() {} // Required for Firebase

    public NotificationModel(String userId, String courseId, String title,
                             String message, long timestamp, boolean isRead, String type) {
        this.userId = userId;
        this.courseId = courseId;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.type = type;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}