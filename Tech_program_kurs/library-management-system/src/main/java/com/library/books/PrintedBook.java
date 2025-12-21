package com.library.books;

public class PrintedBook extends Book {
    private String shelfLocation;
    private int totalPages;

    public PrintedBook(String isbn, String title, String author, int year,
                       String publisher, String shelfLocation, int totalPages) {
        super(isbn, title, author, year, publisher);
        this.shelfLocation = shelfLocation;
        this.totalPages = totalPages;
    }

    @Override
    public String getBookInfo() {
        return String.format("Printed Book: %s by %s (%d), Location: %s, Pages: %d",
                title, author, year, shelfLocation, totalPages);
    }

    @Override
    public PrintedBook clone() {
        return new PrintedBook(isbn, title, author, year, publisher, shelfLocation, totalPages);
    }

    // Getters
    public String getShelfLocation() { return shelfLocation; }
    public int getTotalPages() { return totalPages; }
}