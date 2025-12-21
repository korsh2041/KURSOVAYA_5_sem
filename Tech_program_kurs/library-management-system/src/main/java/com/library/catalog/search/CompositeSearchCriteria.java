package com.library.catalog.search;

import com.library.books.Book;
import java.util.List;

public class CompositeSearchCriteria implements SearchCriteria {
    private List<SearchCriteria> criteria;

    public CompositeSearchCriteria(List<SearchCriteria> criteria) {
        this.criteria = criteria;
    }

    @Override
    public boolean matches(Book book) {
        for (SearchCriteria criterion : criteria) {
            if (!criterion.matches(book)) {
                return false;
            }
        }
        return true;
    }
}