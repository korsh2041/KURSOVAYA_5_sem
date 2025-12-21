package com.library.loans.state;

import com.library.loans.Loan;

public class OverdueState implements LoanState {
    @Override
    public void handleReturn(Loan loan) {
        loan.setState(new ReturnedState());
    }

    @Override
    public void handleOverdue(Loan loan) {
        // Уже просрочено
    }

    @Override
    public String getStateName() {
        return "OVERDUE";
    }
}