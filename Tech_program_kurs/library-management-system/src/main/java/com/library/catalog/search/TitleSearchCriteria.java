package com.library.catalog.search;

import com.library.books.Book;

public class TitleSearchCriteria implements SearchCriteria {
    private String title;

    public TitleSearchCriteria(String title) {
        this.title = title.toLowerCase();
    }

    @Override
    public boolean matches(Book book) {
        return book.getTitle().toLowerCase().contains(title);
    }
}