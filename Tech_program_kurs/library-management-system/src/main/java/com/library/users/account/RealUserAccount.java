package com.library.users.account;

import com.library.users.Professor;
import com.library.users.Student;
import com.library.users.User;
import com.library.loans.Loan;
import java.util.ArrayList;
import java.util.List;

public class RealUserAccount implements AccountModule {
    private User user;
    private List<Loan> loanHistory;
    private List<String> personalData;

    public RealUserAccount(User user) {
        this.user = user;
        this.loanHistory = new ArrayList<>();
        this.personalData = new ArrayList<>();
        initializePersonalData();
    }

    private void initializePersonalData() {
        personalData.add("Email: " + user.getEmail());
        personalData.add("Phone: " + user.getPhone());
        personalData.add("Registration: " + user.getRegistrationDate());

        if (user instanceof Student) {
            personalData.add("Student ID: " + ((Student) user).getStudentId());
            personalData.add("Department: " + ((Student) user).getDepartment());
        } else if (user instanceof Professor) {
            personalData.add("Employee ID: " + ((Professor) user).getEmployeeId());
            personalData.add("Faculty: " + ((Professor) user).getFaculty());
        }
    }

    @Override
    public List<Loan> getLoanHistory() {
        System.out.println("Получение полной истории выдачи для " + user.getName());
        return new ArrayList<>(loanHistory);
    }

    @Override
    public List<String> getPersonalData() {
        System.out.println("Получение персональных данных для " + user.getName());
        return new ArrayList<>(personalData);
    }

    @Override
    public void addLoan(Loan loan) {
        loanHistory.add(loan);
        System.out.println("Добавлена выдача в историю: " + loan.getLoanId());
    }

    @Override
    public void updatePersonalData(String newData) {
        personalData.add("Updated: " + new java.util.Date() + " - " + newData);
        System.out.println("Обновлены персональные данные для " + user.getName());
    }

    public User getUser() {
        return user;
    }
}