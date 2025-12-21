package com.library.loans;

import com.library.users.User;
import com.library.books.BookCopy;
import com.library.loans.state.LoanState;
import com.library.loans.state.IssuedState;
import java.util.Date;

public class Loan {
    private String loanId;
    private User user;
    private BookCopy bookCopy;
    private Date issueDate;
    private Date dueDate;
    private Date returnDate;
    private LoanState state;

    public Loan(String loanId, User user, BookCopy bookCopy, Date issueDate, Date dueDate) {
        this.loanId = loanId;
        this.user = user;
        this.bookCopy = bookCopy;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.state = new IssuedState();
    }

    public void returnBook() {
        state.handleReturn(this);
        this.returnDate = new Date();
        bookCopy.setAvailable(true);
    }

    public void markOverdue() {
        state.handleOverdue(this);
    }

    // Getters and setters
    public void setState(LoanState state) {
        this.state = state;
    }

    public String getLoanId() { return loanId; }
    public User getUser() { return user; }
    public BookCopy getBookCopy() { return bookCopy; }
    public Date getIssueDate() { return issueDate; }
    public Date getDueDate() { return dueDate; }
    public Date getReturnDate() { return returnDate; }
    public LoanState getState() { return state; }

    @Override
    public String toString() {
        return String.format("Loan{id=%s, user=%s, book=%s, state=%s}",
                loanId, user.getName(), bookCopy.getBook().getTitle(), state.getStateName());
    }
}