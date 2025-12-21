package com.library.catalog.search;

import com.library.books.Book;

public interface SearchCriteria {
    boolean matches(Book book);
}