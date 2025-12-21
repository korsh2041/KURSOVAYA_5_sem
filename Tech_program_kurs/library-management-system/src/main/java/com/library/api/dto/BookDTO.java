package com.library.api.dto;

public class BookDTO {
    private String isbn;
    private String title;
    private String author;
    private int year;
    private String publisher;
    private String type;
    private String info;
    private int totalCopies;
    private int availableCopies;

    public BookDTO() {}

    public BookDTO(String isbn, String title, String author, int year, String publisher, 
                   String type, String info, int totalCopies, int availableCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.publisher = publisher;
        this.type = type;
        this.info = info;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // Getters and Setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
}
