package com.example.merainstitue;

public class CardItem {
    private String title;
    private String subtitle;
    private int progress;
    private String completionStatus;
    private String duration;
    private String lessons;

    public CardItem(String title, String subtitle, int progress, String completionStatus, String duration, String lessons) {
        this.title = title;
        this.subtitle = subtitle;
        this.progress = progress;
        this.completionStatus = completionStatus;
        this.duration = duration;
        this.lessons = lessons;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getProgress() {
        return progress;
    }

    public String getCompletionStatus() {
        return completionStatus;
    }

    public String getDuration() {
        return duration;
    }

    public String getLessons() {
        return lessons;
    }
}
