package com.library.api.controller;

import com.library.api.dto.LoanDTO;
import com.library.persistence.LibraryRepository;
import com.library.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LibraryRepository repository;

    @Autowired
    public LoanController(LibraryRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/borrow")
    public LoanDTO borrowBook(@RequestParam String userId, @RequestParam String isbn) {
        try {
            // В реальной системе здесь будет бизнес-логика займа
            // Пока возвращаем mock ответ
            LoanDTO loan = new LoanDTO();
            loan.setId("LOAN_" + System.currentTimeMillis());
            loan.setIsbn(isbn);
            loan.setBorrowedAt(java.time.LocalDate.now().toString());
            loan.setDueAt(java.time.LocalDate.now().plusMonths(1).toString());
            loan.setFinePaid(false);
            loan.setFine(0);
            return loan;
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/return")
    public LoanDTO returnBook(@RequestParam String loanId) {
        try {
            LoanDTO loan = new LoanDTO();
            loan.setId(loanId);
            loan.setFinePaid(false);
            return loan;
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/user/{userId}")
    public List<LoanDTO> getUserLoans(@PathVariable String userId) {
        try {
            // В реальной системе здесь будет получение займов пользователя
            return new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/overdue")
    public List<LoanDTO> getOverdueLoans() {
        try {
            return new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @PostMapping("/pay-fine")
    public LoanDTO payFine(@RequestParam String loanId, @RequestParam double amount) {
        try {
            LoanDTO loan = new LoanDTO();
            loan.setId(loanId);
            loan.setFinePaid(true);
            loan.setFine(0);
            return loan;
        } catch (Exception e) {
            return null;
        }
    }
}
