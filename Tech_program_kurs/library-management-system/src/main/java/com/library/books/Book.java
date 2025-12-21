package com.library.books;

public abstract class Book implements Cloneable {
    protected String isbn;
    protected String title;
    protected String author;
    protected int year;
    protected String publisher;

    public Book(String isbn, String title, String author, int year, String publisher) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.publisher = publisher;
    }

    // Getters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public String getPublisher() { return publisher; }

    @Override
    public abstract Book clone();
    public abstract String getBookInfo();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return isbn.hashCode();
    }
}