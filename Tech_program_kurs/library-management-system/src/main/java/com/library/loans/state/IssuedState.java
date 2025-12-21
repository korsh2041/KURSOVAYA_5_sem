package com.library.loans.state;

import com.library.loans.Loan;

public class IssuedState implements LoanState {
    @Override
    public void handleReturn(Loan loan) {
        loan.setState(new ReturnedState());
    }

    @Override
    public void handleOverdue(Loan loan) {
        loan.setState(new OverdueState());
    }

    @Override
    public String getStateName() {
        return "ISSUED";
    }
}