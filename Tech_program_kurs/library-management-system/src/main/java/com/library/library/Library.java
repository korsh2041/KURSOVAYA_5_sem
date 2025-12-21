package com.library.library;

import com.library.books.Book;
import com.library.books.BookCopy;
import com.library.users.User;
import com.library.loans.Loan;
import com.library.catalog.Catalog;
import com.library.notifications.NotificationService;
import java.util.*;

public class Library {
    private static Library instance;
    private Catalog catalog;
    private List<User> users;
    private List<BookCopy> bookCopies;
    private List<Loan> activeLoans;
    private List<Loan> loanHistory;
    private NotificationService notificationService;

    private Library() {
        this.catalog = new Catalog();
        this.users = new ArrayList<>();
        this.bookCopies = new ArrayList<>();
        this.activeLoans = new ArrayList<>();
        this.loanHistory = new ArrayList<>();
        this.notificationService = new NotificationService();
    }

    public static synchronized Library getInstance() {
        if (instance == null) {
            instance = new Library();
        }
        return instance;
    }

    public void addUser(User user) {
        users.add(user);
        notificationService.registerObserver(user);
    }

    public void addBookCopy(BookCopy bookCopy) {
        bookCopies.add(bookCopy);
        catalog.addBook(bookCopy.getBook());
    }

    public Loan issueBook(User user, BookCopy bookCopy) {
        if (!bookCopy.isAvailable()) {
            throw new IllegalStateException("Копия книги недоступна");
        }

        if (!user.canBorrowBook(getUserActiveLoans(user).size())) {
            throw new IllegalStateException("Пользователь не может взять больше книг");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, user.getLoanPeriod());
        Date dueDate = calendar.getTime();

        Loan loan = new Loan(generateLoanId(), user, bookCopy, new Date(), dueDate);

        bookCopy.setAvailable(false);
        activeLoans.add(loan);

        return loan;
    }

    public void returnBook(Loan loan) {
        loan.returnBook();
        activeLoans.remove(loan);
        loanHistory.add(loan);
    }

    public List<Loan> getUserActiveLoans(User user) {
        return activeLoans.stream()
                .filter(loan -> loan.getUser().equals(user))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private String generateLoanId() {
        return "LOAN_" + System.currentTimeMillis();
    }

    // Getters
    public Catalog getCatalog() { return catalog; }
    public List<User> getUsers() { return new ArrayList<>(users); }
    public List<BookCopy> getBookCopies() { return new ArrayList<>(bookCopies); }
    public List<Loan> getActiveLoans() { return new ArrayList<>(activeLoans); }
    public List<Loan> getLoanHistory() { return new ArrayList<>(loanHistory); }
    public NotificationService getNotificationService() { return notificationService; }
}