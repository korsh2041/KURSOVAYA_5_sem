package com.library.users.account;

import com.library.users.Librarian;
import com.library.users.Professor;
import com.library.users.Student;
import com.library.users.User;
import com.library.loans.Loan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserAccountProxy implements AccountModule {
    private RealUserAccount realAccount;
    private User currentUser;

    public UserAccountProxy(User currentUser, User targetUser) {
        this.currentUser = currentUser;
        this.realAccount = new RealUserAccount(targetUser);
    }

    private boolean hasAccess() {
        if (currentUser instanceof Librarian) {
            return true;
        }

        if (currentUser.equals(realAccount.getUser())) {
            return true;
        }

        if (currentUser instanceof Professor && realAccount.getUser() instanceof Student) {
            return true;
        }

        return false;
    }

    private boolean hasFullAccess() {
        return currentUser instanceof Librarian || currentUser.equals(realAccount.getUser());
    }

    @Override
    public List<Loan> getLoanHistory() {
        if (!hasAccess()) {
            System.out.println("Доступ запрещен: " + currentUser.getName() +
                    " не может просматривать историю выдачи " + realAccount.getUser().getName());
            return new ArrayList<>();
        }

        if (!hasFullAccess()) {
            System.out.println("Ограниченный доступ: показана только основная информация");
            return realAccount.getLoanHistory().stream()
                    .limit(5)
                    .collect(Collectors.toList());
        }

        return realAccount.getLoanHistory();
    }

    @Override
    public List<String> getPersonalData() {
        if (!hasAccess()) {
            System.out.println("Доступ запрещен: " + currentUser.getName() +
                    " не может просматривать персональные данные " + realAccount.getUser().getName());
            return new ArrayList<>();
        }

        if (!hasFullAccess()) {
            System.out.println("Ограниченный доступ: показана только основная информация");
            return realAccount.getPersonalData().stream()
                    .filter(data -> !data.contains("Email") && !data.contains("Phone"))
                    .collect(Collectors.toList());
        }

        return realAccount.getPersonalData();
    }

    @Override
    public void addLoan(Loan loan) {
        if (!hasFullAccess()) {
            System.out.println("Доступ запрещен: только библиотекари могут добавлять записи выдачи");
            return;
        }
        realAccount.addLoan(loan);
    }

    @Override
    public void updatePersonalData(String newData) {
        if (!hasFullAccess()) {
            System.out.println("Доступ запрещен: только владелец аккаунта может обновлять данные");
            return;
        }
        realAccount.updatePersonalData(newData);
    }

    public User getUser() {
        return realAccount.getUser();
    }
}