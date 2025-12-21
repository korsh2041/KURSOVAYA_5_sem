package com.library.books;

public class EBook extends Book {
    private double fileSize;
    private String format;

    public EBook(String isbn, String title, String author, int year,
                 String publisher, double fileSize, String format) {
        super(isbn, title, author, year, publisher);
        this.fileSize = fileSize;
        this.format = format;
    }

    @Override
    public String getBookInfo() {
        return String.format("EBook: %s by %s (%d), Format: %s, Size: %.2fMB",
                title, author, year, format, fileSize);
    }

    @Override
    public EBook clone() {
        return new EBook(isbn, title, author, year, publisher, fileSize, format);
    }

    // Getters
    public double getFileSize() { return fileSize; }
    public String getFormat() { return format; }
}