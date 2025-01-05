package com.example.merainstitue;

public class CardItem {
    private String id;
    private String courseId;
    private String title;
    private String description;
    private String imageBase64;
    private String teacherId;
    private int lessonCount;
    private Double price; // New field for price

    // Empty constructor for Firestore
    public CardItem() {}

    // Constructor
    public CardItem(String id, String courseId, String title, String description, String imageBase64, String teacherId, int lessonCount,  Double price) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.imageBase64 = imageBase64;
        this.teacherId = teacherId;
        this.lessonCount = lessonCount;
        this.price = price;
    }

    // Getter and setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and setter for courseId
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    // Getter and setter for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter and setter for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and setter for imageBase64
    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    // Getter and setter for teacherId
    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    // Getter and setter for lessonCount
    public int getLessonCount() {
        return lessonCount;
    }

    public void setLessonCount(int lessonCount) {
        this.lessonCount = lessonCount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
