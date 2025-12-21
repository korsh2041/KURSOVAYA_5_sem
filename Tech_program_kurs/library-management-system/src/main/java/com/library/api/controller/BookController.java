package com.library.api.controller;

import com.library.api.dto.BookDTO;
import com.library.books.Book;
import com.library.persistence.LibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final LibraryRepository repository;

    @Autowired
    public BookController(LibraryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<BookDTO> getAllBooks() {
        try {
            return repository.findAllBooks().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/{isbn}")
    public BookDTO getBook(@PathVariable String isbn) {
        try {
            Book book = repository.findBookByIsbn(isbn).orElse(null);
            if (book != null) {
                return convertToDTO(book);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/search/title")
    public List<BookDTO> searchByTitle(@RequestParam String query) {
        try {
            return repository.findAllBooks().stream()
                    .filter(b -> b.getTitle().toLowerCase().contains(query.toLowerCase()))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/search/author")
    public List<BookDTO> searchByAuthor(@RequestParam String query) {
        try {
            return repository.findAllBooks().stream()
                    .filter(b -> b.getAuthor().toLowerCase().contains(query.toLowerCase()))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/search/year")
    public List<BookDTO> searchByYear(@RequestParam int year) {
        try {
            return repository.findAllBooks().stream()
                    .filter(b -> b.getYear() == year)
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private BookDTO convertToDTO(Book book) {
        int totalCopies = (int) repository.findAllCopies().stream()
                .filter(c -> c.getBook().getIsbn().equals(book.getIsbn()))
                .count();
        int availableCopies = (int) repository.findAllCopies().stream()
                .filter(c -> c.getBook().getIsbn().equals(book.getIsbn()) && c.isAvailable())
                .count();

        return new BookDTO(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getYear(),
                book.getPublisher(),
                book.getClass().getSimpleName(),
                "",
                totalCopies,
                availableCopies
        );
    }
}
