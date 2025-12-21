package com.library.persistence;

import com.library.books.Book;
import com.library.books.BookCopy;
import com.library.users.User;
import com.library.loans.Loan;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LibraryRepository {
    // Book operations
    void saveBook(Book book);
    Optional<Book> findBookByIsbn(String isbn);
    List<Book> findAllBooks();
    List<Book> findBooksByTitle(String title);
    List<Book> findBooksByAuthor(String author);
    void deleteBook(String isbn);

    // BookCopy operations
    void saveBookCopy(BookCopy copy);
    Optional<BookCopy> findBookCopyById(String copyId);
    List<BookCopy> findAvailableCopies(Book book);
    List<BookCopy> findAllCopies();
    void updateBookCopy(BookCopy copy);

    // User operations
    void saveUser(User user);
    void saveUser(User user, String password);
    Optional<User> findUserById(String userId);
    Optional<User> findUserByName(String name);
    Optional<User> findUserByCredentials(String username, String password);
    List<User> findAllUsers();
    List<User> findUsersByType(Class<?> userType);
    void deleteUser(String userId);
    boolean validatePassword(String userId, String password);

    // Loan operations
    void saveLoan(Loan loan);
    Optional<Loan> findLoanById(String loanId);
    List<Loan> findActiveLoansByUser(String userId);
    List<Loan> findOverdueLoans();
    List<Loan> findAllLoans();
    void updateLoan(Loan loan);

    // Statistics
    int getBookCount();
    int getUserCount();
    int getActiveLoanCount();
    int getOverdueLoanCount();

    // Новые методы для работы с поставками
    default List<Loan> findLoansDueSoon(int days) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        Date warningDate = calendar.getTime();

        return findAllLoans().stream()
                .filter(loan -> loan.getReturnDate() == null)
                .filter(loan -> !loan.getDueDate().before(now))
                .filter(loan -> loan.getDueDate().before(warningDate) || loan.getDueDate().equals(warningDate))
                .collect(java.util.stream.Collectors.toList());
    }
}