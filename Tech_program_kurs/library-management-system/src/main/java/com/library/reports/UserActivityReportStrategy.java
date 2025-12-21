package com.library.reports;

import com.library.library.Library;
import com.library.users.User;
import com.library.loans.Loan;
import java.util.Map;
import java.util.stream.Collectors;

public class UserActivityReportStrategy implements ReportStrategy {
    @Override
    public void generateReport(Library library) {
        System.out.println("=== ОТЧЕТ ПО АКТИВНОСТИ ПОЛЬЗОВАТЕЛЕЙ ===");

        Map<User, Long> userActivity = library.getLoanHistory().stream()
                .collect(Collectors.groupingBy(
                        Loan::getUser,
                        Collectors.counting()
                ));

        userActivity.entrySet().stream()
                .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
                .forEach(entry ->
                        System.out.println(" - " + entry.getKey().getName() +
                                " (" + entry.getKey().getClass().getSimpleName() +
                                "): " + entry.getValue() + " выдач")
                );
    }

    @Override
    public String getReportName() {
        return "Отчет по активности пользователей";
    }
}