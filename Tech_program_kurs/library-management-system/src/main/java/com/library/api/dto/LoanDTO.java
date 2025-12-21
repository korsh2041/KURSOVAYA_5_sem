package com.library.api.dto;

public class LoanDTO {
    private String id;
    private String isbn;
    private String title;
    private String author;
    private String borrowedAt;
    private String dueAt;
    private boolean finePaid;
    private double fine;

    public LoanDTO() {}

    public LoanDTO(String id, String isbn, String title, String author, String borrowedAt, 
                   String dueAt, boolean finePaid, double fine) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.borrowedAt = borrowedAt;
        this.dueAt = dueAt;
        this.finePaid = finePaid;
        this.fine = fine;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getBorrowedAt() { return borrowedAt; }
    public void setBorrowedAt(String borrowedAt) { this.borrowedAt = borrowedAt; }

    public String getDueAt() { return dueAt; }
    public void setDueAt(String dueAt) { this.dueAt = dueAt; }

    public boolean isFinePaid() { return finePaid; }
    public void setFinePaid(boolean finePaid) { this.finePaid = finePaid; }

    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }
}
