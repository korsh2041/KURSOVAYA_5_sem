package com.library.migration;

import com.library.persistence.jpa.entity.BookEntity;
import com.library.persistence.jpa.entity.LogEntity;
import com.library.persistence.jpa.entity.UserEntity;
import com.library.persistence.jpa.repository.BookRepository;
import com.library.persistence.jpa.repository.LogRepository;
import com.library.persistence.jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import java.util.UUID;

@Component
public class DataMigrationRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LogRepository logRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${migrate:false}")
    private boolean migrate;

    private static final String DATA_DIR = "library_data";

    public DataMigrationRunner(UserRepository userRepository, BookRepository bookRepository, LogRepository logRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.logRepository = logRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!migrate) {
            System.out.println("DataMigrationRunner: migrate=false, пропускаю импорт данных.");
            return;
        }

        System.out.println("DataMigrationRunner: начинаю импорт из файлов в БД...");

        importUsers();
        importBooks();
        importLogs();

        System.out.println("DataMigrationRunner: импорт завершён.");
    }

    private void importUsers() {
        Path usersDir = Paths.get(DATA_DIR, "users");
        if (!Files.exists(usersDir)) return;

        try (Stream<Path> paths = Files.list(usersDir)) {
            paths.filter(p -> p.getFileName().toString().endsWith(".txt") && !p.getFileName().toString().contains("_loans"))
                    .forEach(p -> {
                        try {
                            migrateUserFile(p.toFile());
                        } catch (Exception e) {
                            System.err.println("Ошибка импорта пользователя из " + p + ": " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            System.err.println("Ошибка чтения директории users: " + e.getMessage());
        }
    }

    private void migrateUserFile(File file) throws Exception {
        String userId = null, userType = null, name = null, email = null, phone = null, password = null, additionalInfo = null, registeredAt = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder addInfoBuf = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID: ")) userId = line.substring("ID: ".length()).trim();
                else if (line.startsWith("Тип: ")) userType = line.substring("Тип: ".length()).trim();
                else if (line.startsWith("Имя: ")) name = line.substring("Имя: ".length()).trim();
                else if (line.startsWith("Email: ")) email = line.substring("Email: ".length()).trim();
                else if (line.startsWith("Телефон: ")) phone = line.substring("Телефон: ".length()).trim();
                else if (line.startsWith("Пароль: ")) password = line.substring("Пароль: ".length()).trim();
                else if (line.startsWith("Доп.информация: ")) addInfoBuf.append(line.substring("Доп.информация: ".length()).trim());
                else if (line.startsWith("Дата регистрации: ")) registeredAt = line.substring("Дата регистрации: ".length()).trim();
            }
            additionalInfo = addInfoBuf.toString();
        }

        if (userId == null || name == null) return;

        UUID uuid = uuidFromString(userId);
        if (userRepository.existsById(uuid)) {
            System.out.println("Пользователь уже в БД: " + userId);
            return;
        }

        UserEntity ue = new UserEntity();
        ue.setId(uuid);
        ue.setUserType(userType == null ? "Unknown" : userType);
        ue.setName(name);
        ue.setEmail(email);
        ue.setPhone(phone);
        if (password == null) password = ""; // если нет пароля
        ue.setPasswordHash(passwordEncoder.encode(password));
        ue.setAdditionalInfo(additionalInfo);
        if (registeredAt != null) {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
                ue.setRegisteredAt(LocalDateTime.parse(registeredAt, fmt));
            } catch (Exception ex) {
                // ignore parse, prePersist будет выставлять now
            }
        }

        userRepository.save(ue);
        System.out.println("Импортирован пользователь: " + name + " (" + userId + ")");
    }

    private void importBooks() {
        Path booksDir = Paths.get(DATA_DIR, "books");
        if (!Files.exists(booksDir)) return;

        try (Stream<Path> paths = Files.list(booksDir)) {
            paths.filter(p -> p.getFileName().toString().endsWith(".txt") && !p.getFileName().toString().equals("copies.txt"))
                    .forEach(p -> {
                        try {
                            migrateBookFile(p.toFile());
                        } catch (Exception e) {
                            System.err.println("Ошибка импорта книги из " + p + ": " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            System.err.println("Ошибка чтения директории books: " + e.getMessage());
        }
    }

    private void migrateBookFile(File file) throws Exception {
        String isbn = null, title = null, author = null, publisher = null, bookType = null, additionalInfo = null;
        Integer year = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder addInfoBuf = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ISBN: ")) isbn = line.substring("ISBN: ".length()).trim();
                else if (line.startsWith("Название: ")) title = line.substring("Название: ".length()).trim();
                else if (line.startsWith("Автор: ")) author = line.substring("Автор: ".length()).trim();
                else if (line.startsWith("Год: ")) {
                    try { year = Integer.parseInt(line.substring("Год: ".length()).trim()); } catch (Exception ex) {}
                }
                else if (line.startsWith("Издательство: ")) publisher = line.substring("Издательство: ".length()).trim();
                else if (line.startsWith("Тип: ")) bookType = line.substring("Тип: ".length()).trim();
                else if (line.startsWith("Доп.информация: ")) addInfoBuf.append(line.substring("Доп.информация: ".length()).trim());
            }
            additionalInfo = addInfoBuf.toString();
        }

        if (isbn == null || title == null) return;

        if (bookRepository.existsById(isbn)) {
            System.out.println("Книга уже в БД: " + isbn);
            return;
        }

        BookEntity be = new BookEntity();
        be.setIsbn(isbn);
        be.setTitle(title);
        be.setAuthor(author);
        be.setYear(year);
        be.setPublisher(publisher);
        be.setBookType(bookType);
        be.setAdditionalInfo(additionalInfo);

        bookRepository.save(be);
        System.out.println("Импортирована книга: " + title + " (" + isbn + ")");
    }

    private void importLogs() {
        Path logsDir = Paths.get(DATA_DIR, "logs");
        if (!Files.exists(logsDir)) return;

        try (Stream<Path> paths = Files.list(logsDir)) {
            paths.filter(p -> p.getFileName().toString().startsWith("actions_") && p.getFileName().toString().endsWith(".txt"))
                    .forEach(p -> {
                        try {
                            migrateLogFile(p.toFile());
                        } catch (Exception e) {
                            System.err.println("Ошибка импорта логов из " + p + ": " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            System.err.println("Ошибка чтения директории logs: " + e.getMessage());
        }
    }

    private void migrateLogFile(File file) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // формат: dd.MM.yyyy HH:mm:ss | ACTION | details
                String[] parts = line.split(" \\| ", 3);
                if (parts.length >= 2) {
                    String datePart = parts[0].trim();
                    String action = parts[1].trim();
                    String details = parts.length == 3 ? parts[2].trim() : null;

                    LogEntity le = new LogEntity();
                    le.setAction(action);
                    le.setDetails(details);
                    // createdAt is set by @PrePersist
                    logRepository.save(le);
                }
            }
        }
    }

    private UUID uuidFromString(String s) {
        try {
            // если строка уже UUID
            return UUID.fromString(s);
        } catch (Exception e) {
            // детерминированный UUID на основе имени
            return UUID.nameUUIDFromBytes(s.getBytes());
        }
    }
}
