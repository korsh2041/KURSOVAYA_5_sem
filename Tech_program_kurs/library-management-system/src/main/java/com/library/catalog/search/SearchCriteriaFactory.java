package com.library.catalog.search;

import java.util.List;

public class SearchCriteriaFactory {
    public static SearchCriteria createTitleCriteria(String title) {
        return new TitleSearchCriteria(title);
    }

    public static SearchCriteria createAuthorCriteria(String author) {
        return new AuthorSearchCriteria(author);
    }

    public static SearchCriteria createYearCriteria(int year) {
        return new YearSearchCriteria(year);
    }

    public static SearchCriteria createCompositeCriteria(List<SearchCriteria> criteria) {
        return new CompositeSearchCriteria(criteria);
    }
}