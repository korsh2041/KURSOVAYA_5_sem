package com.library.catalog.search;

import com.library.books.Book;

public class AuthorSearchCriteria implements SearchCriteria {
    private String author;

    public AuthorSearchCriteria(String author) {
        this.author = author.toLowerCase();
    }

    @Override
    public boolean matches(Book book) {
        return book.getAuthor().toLowerCase().contains(author);
    }
}