package com.library.books;

import java.util.Date;

public class BookCopy implements Cloneable {
    private String copyId;
    private Book book;
    private boolean isAvailable;
    private Date acquisitionDate;

    public BookCopy(String copyId, Book book, Date acquisitionDate) {
        this.copyId = copyId;
        this.book = book;
        this.isAvailable = true;
        this.acquisitionDate = acquisitionDate;
    }

    @Override
    public BookCopy clone() {
        try {
            BookCopy cloned = (BookCopy) super.clone();
            cloned.book = this.book.clone();
            cloned.acquisitionDate = new Date(this.acquisitionDate.getTime());
            cloned.isAvailable = true;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Клонирование не поддерживается", e);
        }
    }

    // Getters and setters
    public String getCopyId() { return copyId; }
    public Book getBook() { return book; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public Date getAcquisitionDate() { return acquisitionDate; }

    @Override
    public String toString() {
        return String.format("BookCopy{id=%s, book=%s, available=%s}",
                copyId, book.getTitle(), isAvailable);
    }
}