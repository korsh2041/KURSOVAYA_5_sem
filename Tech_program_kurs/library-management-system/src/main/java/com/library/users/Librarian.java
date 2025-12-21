package com.library.users;

import com.library.books.Book;
import com.library.catalog.Catalog;
import com.library.books.BookCopy;
import java.util.Date;

public class Librarian extends User {
    private String librarianId;
    private String shift;

    public Librarian(String userId, String name, String email, String phone,
                     String librarianId, String shift) {
        super(userId, name, email, phone);
        this.librarianId = librarianId;
        this.shift = shift;
    }

    @Override
    public int getMaxBooksLimit() {
        return 15;
    }

    @Override
    protected boolean hasBorrowingRights() {
        return true;
    }

    @Override
    protected int calculateLoanPeriod() {
        return 90;
    }

    public void addBookToCatalog(Book book, Catalog catalog) {
        catalog.addBook(book);
    }

    public void removeBookFromCatalog(Book book, Catalog catalog) {
        catalog.removeBook(book);
    }

    // Новые методы для работы с поставками
    public Book addNewBook(String isbn, String title, String author, int year,
                           String publisher, String bookType, String additionalInfo) {
        // Этот метод будет вызываться из Main.java
        return null;
    }

    public BookCopy addBookCopy(String copyId, Book book, Date acquisitionDate) {
        return new BookCopy(copyId, book, acquisitionDate);
    }

    public void recordBookSupply(String supplyId, String supplier, String date,
                                 int bookCount, int copyCount, double totalCost) {
        // Записываем информацию о поставке в лог
        String logMessage = String.format(
                "Поставка #%s от %s (%s): %d книг, %d копий, стоимость: %.2f руб.",
                supplyId, supplier, date, bookCount, copyCount, totalCost
        );

        System.out.println("📦 Записана информация о поставке: " + logMessage);
    }

    public String getLibrarianId() { return librarianId; }
    public String getShift() { return shift; }

    // Новый метод для получения информации о библиотекаре
    public String getLibrarianInfo() {
        return String.format(
                "Библиотекарь: %s (ID: %s)\n" +
                        "Смена: %s\n" +
                        "Email: %s\n" +
                        "Телефон: %s",
                getName(), getUserId(), shift, getEmail(), getPhone()
        );
    }
}