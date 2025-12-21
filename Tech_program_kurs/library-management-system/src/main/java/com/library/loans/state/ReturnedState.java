package com.library.loans.state;

import com.library.loans.Loan;

public class ReturnedState implements LoanState {
    @Override
    public void handleReturn(Loan loan) {
        // Уже возвращено
    }

    @Override
    public void handleOverdue(Loan loan) {
        // Не может стать просроченным после возврата
    }

    @Override
    public String getStateName() {
        return "RETURNED";
    }
}