package com.example.merainstitue.lesson;

public class Lesson {
    private String id;              // Unique ID for the lesson
    private String title;           // Title of the lesson
    private String description;     // Description of the lesson
    private String videoUrl;        // URL of the uploaded video (stored in Cloudinary)
    private String thumbnailBase64; // Base64 representation of the lesson thumbnail image
    private String courseId;        // ID of the associated course

    // Default constructor for Firestore
    public Lesson() {
    }

    // Parameterized constructor
    public Lesson(String id, String title, String description, String videoUrl, String thumbnailBase64, String courseId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailBase64 = thumbnailBase64;
        this.courseId = courseId;
    }

    // Corrected constructor to set the necessary fields
    public Lesson(String title, String description, String videoUrl, String thumbnailBase64,String courseId) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.courseId = courseId;
        this.thumbnailBase64 = thumbnailBase64;
    }

    // Getters and Setters
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailBase64() {
        return thumbnailBase64;
    }

    public void setThumbnailBase64(String thumbnailBase64) {
        this.thumbnailBase64 = thumbnailBase64;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
