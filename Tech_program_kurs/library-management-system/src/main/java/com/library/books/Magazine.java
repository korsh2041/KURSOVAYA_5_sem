package com.library.books;

import java.util.Date;

public class Magazine extends Book {
    private String issue;
    private Date publicationDate;

    public Magazine(String isbn, String title, String author, int year,
                    String publisher, String issue, Date publicationDate) {
        super(isbn, title, author, year, publisher);
        this.issue = issue;
        this.publicationDate = publicationDate;
    }

    @Override
    public String getBookInfo() {
        return String.format("Magazine: %s, Issue: %s, Published: %s",
                title, issue, publicationDate);
    }

    @Override
    public Magazine clone() {
        return new Magazine(isbn, title, author, year, publisher, issue,
                new Date(publicationDate.getTime()));
    }

    // Getters
    public String getIssue() { return issue; }
    public Date getPublicationDate() { return publicationDate; }
}