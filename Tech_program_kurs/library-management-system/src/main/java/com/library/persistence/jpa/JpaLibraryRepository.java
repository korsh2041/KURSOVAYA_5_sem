package com.library.persistence.jpa;

import com.library.books.Book;
import com.library.books.BookCopy;
import com.library.books.PrintedBook;
import com.library.loans.Loan;
import com.library.persistence.LibraryRepository;
import com.library.persistence.jpa.entity.BookEntity;
import com.library.persistence.jpa.entity.UserEntity;
import com.library.persistence.jpa.repository.BookRepository;
import com.library.persistence.jpa.repository.UserRepository;
import com.library.users.User;
import com.library.users.Student;
import com.library.users.Professor;
import com.library.users.Librarian;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class JpaLibraryRepository implements LibraryRepository {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public JpaLibraryRepository(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    // ========== BOOKS ==========

    @Override
    public void saveBook(Book book) {
        BookEntity be = new BookEntity();
        be.setIsbn(book.getIsbn());
        be.setTitle(book.getTitle());
        be.setAuthor(book.getAuthor());
        be.setYear(book.getYear());
        be.setPublisher(book.getPublisher());
        be.setBookType(book.getClass().getSimpleName());
        bookRepository.save(be);
    }

    @Override
    public Optional<Book> findBookByIsbn(String isbn) {
        return bookRepository.findById(isbn).map(this::convertToBook);
    }

    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToBook)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findBooksByTitle(String title) {
        String searchTitle = title.toLowerCase();
        return bookRepository.findAll().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(searchTitle))
                .map(this::convertToBook)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findBooksByAuthor(String author) {
        String searchAuthor = author.toLowerCase();
        return bookRepository.findAll().stream()
                .filter(b -> b.getAuthor() != null && b.getAuthor().toLowerCase().contains(searchAuthor))
                .map(this::convertToBook)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBook(String isbn) {
        bookRepository.deleteById(isbn);
    }

    // ========== USERS ==========

    @Override
    public void saveUser(User user) {
        saveUser(user, "password");
    }

    @Override
    public void saveUser(User user, String password) {
        UserEntity ue = new UserEntity();
        ue.setId(uuidFromString(user.getUserId()));
        ue.setUserType(user.getClass().getSimpleName());
        ue.setName(user.getName());
        ue.setEmail(user.getEmail());
        ue.setPhone(user.getPhone());
        ue.setPasswordHash(passwordEncoder.encode(password));
        ue.setAdditionalInfo("");
        userRepository.save(ue);
    }

    @Override
    public Optional<User> findUserById(String userId) {
        UUID uuid = uuidFromString(userId);
        return userRepository.findById(uuid).map(this::convertToUser);
    }

    @Override
    public Optional<User> findUserByName(String name) {
        return userRepository.findAll().stream()
                .filter(ue -> ue.getName().equalsIgnoreCase(name))
                .map(this::convertToUser)
                .findFirst();
    }

    @Override
    public Optional<User> findUserByCredentials(String username, String password) {
        return userRepository.findAll().stream()
                .filter(ue -> ue.getName().equalsIgnoreCase(username))
                .filter(ue -> passwordEncoder.matches(password, ue.getPasswordHash()))
                .map(this::convertToUser)
                .findFirst();
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsersByType(Class<?> userType) {
        String typeName = userType.getSimpleName();
        return userRepository.findAll().stream()
                .filter(ue -> ue.getUserType().equals(typeName))
                .map(this::convertToUser)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(String userId) {
        UUID uuid = uuidFromString(userId);
        userRepository.deleteById(uuid);
    }

    @Override
    public boolean validatePassword(String userId, String password) {
        return userRepository.findAll().stream()
                .filter(ue -> ue.getId().toString().equals(userId) || 
                       UUID.nameUUIDFromBytes(userId.getBytes()).equals(ue.getId()))
                .anyMatch(ue -> passwordEncoder.matches(password, ue.getPasswordHash()));
    }

    // ========== BOOK COPIES ==========

    @Override
    public void saveBookCopy(BookCopy copy) {
        // TODO: implement when book_copies table is added
    }

    @Override
    public Optional<BookCopy> findBookCopyById(String copyId) {
        // TODO: implement when book_copies table is added
        return Optional.empty();
    }

    @Override
    public List<BookCopy> findAvailableCopies(Book book) {
        // TODO: implement when book_copies table is added
        return new ArrayList<>();
    }

    @Override
    public List<BookCopy> findAllCopies() {
        // TODO: implement when book_copies table is added
        return new ArrayList<>();
    }

    @Override
    public void updateBookCopy(BookCopy copy) {
        // TODO: implement when book_copies table is added
    }

    // ========== LOANS ==========

    @Override
    public void saveLoan(Loan loan) {
        // TODO: implement when loans table is added
    }

    @Override
    public Optional<Loan> findLoanById(String loanId) {
        // TODO: implement when loans table is added
        return Optional.empty();
    }

    @Override
    public List<Loan> findActiveLoansByUser(String userId) {
        // TODO: implement when loans table is added
        return new ArrayList<>();
    }

    @Override
    public List<Loan> findOverdueLoans() {
        // TODO: implement when loans table is added
        return new ArrayList<>();
    }

    @Override
    public List<Loan> findAllLoans() {
        // TODO: implement when loans table is added
        return new ArrayList<>();
    }

    @Override
    public void updateLoan(Loan loan) {
        // TODO: implement when loans table is added
    }

    // ========== STATISTICS ==========

    @Override
    public int getBookCount() {
        return (int) bookRepository.count();
    }

    @Override
    public int getUserCount() {
        return (int) userRepository.count();
    }

    @Override
    public int getActiveLoanCount() {
        // TODO: implement when loans table is added
        return 0;
    }

    @Override
    public int getOverdueLoanCount() {
        // TODO: implement when loans table is added
        return 0;
    }

    // ========== CONVERSION HELPERS ==========

    private Book convertToBook(BookEntity be) {
        if (be == null) return null;
        
        return new PrintedBook(
                be.getIsbn(),
                be.getTitle(),
                be.getAuthor() != null ? be.getAuthor() : "Unknown",
                be.getYear() != null ? be.getYear() : 0,
                be.getPublisher() != null ? be.getPublisher() : "Unknown",
                "Полка",
                0
        );
    }

    private User convertToUser(UserEntity ue) {
        if (ue == null) return null;
        
        String userId = ue.getId().toString();
        String name = ue.getName();
        String email = ue.getEmail();
        String phone = ue.getPhone();
        String userType = ue.getUserType();

        switch (userType) {
            case "Student":
                return new Student(userId, name, email, phone, "", "");
            case "Professor":
                return new Professor(userId, name, email, phone, "", "");
            case "Librarian":
                return new Librarian(userId, name, email, phone, "", "");
            default:
                return new Student(userId, name, email, phone, "", "");
        }
    }

    private UUID uuidFromString(String s) {
        if (s == null) return UUID.randomUUID();
        try {
            return UUID.fromString(s);
        } catch (Exception e) {
            return UUID.nameUUIDFromBytes(s.getBytes());
        }
    }
}
