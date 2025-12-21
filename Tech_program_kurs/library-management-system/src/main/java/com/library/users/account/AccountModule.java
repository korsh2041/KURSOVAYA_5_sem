package com.library.users.account;

import com.library.loans.Loan;
import java.util.List;

public interface AccountModule {
    List<Loan> getLoanHistory();
    List<String> getPersonalData();
    void addLoan(Loan loan);
    void updatePersonalData(String newData);
}