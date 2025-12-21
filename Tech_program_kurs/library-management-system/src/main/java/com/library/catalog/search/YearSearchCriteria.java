package com.library.catalog.search;

import com.library.books.Book;

public class YearSearchCriteria implements SearchCriteria {
    private int year;

    public YearSearchCriteria(int year) {
        this.year = year;
    }

    @Override
    public boolean matches(Book book) {
        return book.getYear() == year;
    }
}