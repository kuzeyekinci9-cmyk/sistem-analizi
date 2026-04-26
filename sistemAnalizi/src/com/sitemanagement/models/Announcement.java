package com.sitemanagement.models;

import java.time.LocalDate;

public class Announcement {
    private int id;
    private String title;
    private String content;
    private LocalDate publishDate;

    public Announcement(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.publishDate = LocalDate.now(); // Sadece gün bilgisi yeterli
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDate getPublishDate() { return publishDate; }
}