package com.library.loans.state;

import com.library.loans.Loan;

public interface LoanState {
    void handleReturn(Loan loan);
    void handleOverdue(Loan loan);
    String getStateName();
}