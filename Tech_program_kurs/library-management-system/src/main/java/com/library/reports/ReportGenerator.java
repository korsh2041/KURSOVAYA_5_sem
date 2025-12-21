package com.library.reports;

import com.library.library.Library;

public class ReportGenerator {
    private ReportStrategy strategy;

    public void setStrategy(ReportStrategy strategy) {
        this.strategy = strategy;
    }

    public void generateReport(Library library) {
        if (strategy != null) {
            strategy.generateReport(library);
        } else {
            System.out.println("Стратегия отчета не установлена");
        }
    }

    public String getCurrentStrategyName() {
        return strategy != null ? strategy.getReportName() : "Не установлена";
    }
}