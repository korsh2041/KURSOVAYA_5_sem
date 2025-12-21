package com.library.reports;

import com.library.library.Library;
import com.library.loans.Loan;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DateReportStrategy implements ReportStrategy {
    private Date startDate;
    private Date endDate;

    public DateReportStrategy(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void generateReport(Library library) {
        System.out.println("=== ОТЧЕТ ПО ДАТАМ ===");
        System.out.println("Период: " + startDate + " - " + endDate);

        List<Loan> loansInPeriod = library.getLoanHistory().stream()
                .filter(loan -> !loan.getIssueDate().before(startDate) &&
                        !loan.getIssueDate().after(endDate))
                .collect(Collectors.toList());

        System.out.println("Количество выдач: " + loansInPeriod.size());
        System.out.println("Список выдач:");
        loansInPeriod.forEach(loan ->
                System.out.println(" - " + loan.getBookCopy().getBook().getTitle() +
                        " для " + loan.getUser().getName())
        );
    }

    @Override
    public String getReportName() {
        return "Отчет по датам";
    }
}