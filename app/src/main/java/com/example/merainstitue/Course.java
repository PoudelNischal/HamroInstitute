package com.example.merainstitue;

import android.graphics.Bitmap;

import java.util.List;

public class Course {
    private String courseId;      // ID of the course (useful for CRUD operations)
    private String title;         // Title of the course
    private String description;   // Description of the course
    private List<String> tags;    // List of tags for categorization or search
    private String imageBase64; // Base64 image string for course thumbnail
    private String teacherId;     // ID of the teacher who created the course
    private Bitmap thumbnailBitmap;

    // Default constructor for Firebase (required)
    public Course() {
    }

    // Constructor with parameters
    public Course(String title, String description, List<String> tags, String imageBase64, String teacherId) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.imageBase64 = imageBase64;
        this.teacherId = teacherId;
    }

    // Getters and setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String thumbnailBase64) {
        this.imageBase64 = thumbnailBase64;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
}
