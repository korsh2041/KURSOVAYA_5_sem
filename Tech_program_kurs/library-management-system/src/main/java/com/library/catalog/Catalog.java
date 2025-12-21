package com.library.catalog;

import com.library.books.Book;
import com.library.catalog.search.SearchCriteria;
import java.util.ArrayList;
import java.util.List;

public class Catalog {
    private List<Book> books;

    public Catalog() {
        this.books = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    public List<Book> search(SearchCriteria criteria) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (criteria.matches(book)) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public int getBookCount() {
        return books.size();
    }
}