package com.library.duecheck;

import com.library.loans.Loan;
import com.library.users.User;
import com.library.persistence.LibraryRepository;
import java.util.*;
import java.util.stream.Collectors;

public class DueDateChecker {
    private LibraryRepository repository;

    public DueDateChecker(LibraryRepository repository) {
        this.repository = repository;
    }

    public List<Loan> getOverdueLoans() {
        Date now = new Date();
        return repository.findAllLoans().stream()
                .filter(loan -> loan.getReturnDate() == null)
                .filter(loan -> loan.getDueDate().before(now))
                .collect(Collectors.toList());
    }

    public List<Loan> getLoansDueSoon(int daysWarning) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, daysWarning);
        Date warningDate = calendar.getTime();

        return repository.findAllLoans().stream()
                .filter(loan -> loan.getReturnDate() == null)
                .filter(loan -> !loan.getDueDate().before(now))
                .filter(loan -> loan.getDueDate().before(warningDate) || loan.getDueDate().equals(warningDate))
                .collect(Collectors.toList());
    }

    public Map<User, List<Loan>> getOverdueLoansByUser() {
        List<Loan> overdueLoans = getOverdueLoans();
        return overdueLoans.stream()
                .collect(Collectors.groupingBy(Loan::getUser));
    }

    public Map<User, List<Loan>> getLoansDueSoonByUser(int daysWarning) {
        List<Loan> dueSoonLoans = getLoansDueSoon(daysWarning);
        return dueSoonLoans.stream()
                .collect(Collectors.groupingBy(Loan::getUser));
    }

    public void printDueDateReport() {
        List<Loan> overdueLoans = getOverdueLoans();
        List<Loan> dueSoonLoans = getLoansDueSoon(5);

        System.out.println("\n=== ОТЧЕТ ПО СРОКАМ СДАЧИ ===");
        System.out.println("Просроченные выдачи: " + overdueLoans.size());
        System.out.println("Выдачи с истекающим сроком (<=5 дней): " + dueSoonLoans.size());

        if (!overdueLoans.isEmpty()) {
            System.out.println("\n❌ ПРОСРОЧЕННЫЕ ВЫДАЧИ:");
            for (Loan loan : overdueLoans) {
                long daysOverdue = (new Date().getTime() - loan.getDueDate().getTime()) / (1000 * 60 * 60 * 24);
                System.out.println("  • " + loan.getUser().getName() +
                        " - " + loan.getBookCopy().getBook().getTitle() +
                        " (просрочено на " + daysOverdue + " дней)");
            }
        }

        if (!dueSoonLoans.isEmpty()) {
            System.out.println("\n⚠️  ВЫДАЧИ С ИСТЕКАЮЩИМ СРОКОМ:");
            for (Loan loan : dueSoonLoans) {
                long daysLeft = (loan.getDueDate().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24);
                System.out.println("  • " + loan.getUser().getName() +
                        " - " + loan.getBookCopy().getBook().getTitle() +
                        " (осталось " + daysLeft + " дней)");
            }
        }

        if (overdueLoans.isEmpty() && dueSoonLoans.isEmpty()) {
            System.out.println("✅ Все выдачи в порядке. Нет просроченных или истекающих выдач.");
        }
    }

    public double calculateTotalFines() {
        List<Loan> overdueLoans = getOverdueLoans();
        double totalFine = 0;

        for (Loan loan : overdueLoans) {
            long daysOverdue = (new Date().getTime() - loan.getDueDate().getTime()) / (1000 * 60 * 60 * 24);
            totalFine += daysOverdue * 10.0; // 10 рублей в день
        }

        return totalFine;
    }
}