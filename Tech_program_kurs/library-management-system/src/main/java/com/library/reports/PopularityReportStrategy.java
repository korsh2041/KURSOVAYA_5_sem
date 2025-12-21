package com.library.reports;

import com.library.library.Library;
import com.library.books.Book;
import com.library.loans.Loan;
import java.util.Map;
import java.util.stream.Collectors;

public class PopularityReportStrategy implements ReportStrategy {
    @Override
    public void generateReport(Library library) {
        System.out.println("=== ОТЧЕТ ПО ПОПУЛЯРНОСТИ КНИГ ===");

        Map<Book, Long> bookPopularity = library.getLoanHistory().stream()
                .collect(Collectors.groupingBy(
                        loan -> loan.getBookCopy().getBook(),
                        Collectors.counting()
                ));

        bookPopularity.entrySet().stream()
                .sorted(Map.Entry.<Book, Long>comparingByValue().reversed())
                .forEach(entry ->
                        System.out.println(" - " + entry.getKey().getTitle() +
                                ": " + entry.getValue() + " выдач")
                );
    }

    @Override
    public String getReportName() {
        return "Отчет по популярности";
    }
}