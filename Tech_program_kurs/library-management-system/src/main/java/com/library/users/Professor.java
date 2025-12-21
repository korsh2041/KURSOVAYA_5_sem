package com.library.users;

public class Professor extends User {
    private String employeeId;
    private String faculty;

    public Professor(String userId, String name, String email, String phone,
                     String employeeId, String faculty) {
        super(userId, name, email, phone);
        this.employeeId = employeeId;
        this.faculty = faculty;
    }

    @Override
    public int getMaxBooksLimit() {
        return 10;
    }

    @Override
    protected boolean hasBorrowingRights() {
        return true;
    }

    @Override
    protected int calculateLoanPeriod() {
        return 60;
    }

    public String getEmployeeId() { return employeeId; }
    public String getFaculty() { return faculty; }
}