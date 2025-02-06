package com.kitapla;

public class Book {
    private String title;
    private String author;
    private String imageUrl;
    private String description;

    public Book() {
        // Firestore için boş kurucu
    }

    public Book(String title, String author, String imageUrl, String description) {
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }
}
