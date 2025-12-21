package com.library.reports;

import com.library.library.Library;

public interface ReportStrategy {
    void generateReport(Library library);
    String getReportName();
}